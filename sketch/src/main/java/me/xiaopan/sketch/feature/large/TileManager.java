/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.feature.large;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.util.ObjectPool;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 碎片管理器
 */
// TODO: 16/8/31 对象池的对象的回收好好查一下
// TODO: 16/8/31 有些许的卡顿感，看怎么优化比较好，例如控制刷新率，或者降低图片数量
// TODO: 16/9/3 优化一下绘制区域，有的时候会变得比较大
// TODO: 16/9/3 将分块管理的代码抽离
public class TileManager {
    private static final String NAME = "TileManager";

    private Context context;
    private LargeImageViewer largeImageViewer;

    private int tiles = 3;
    private Rect drawRect = new Rect();
    private Rect srcRect = new Rect();

    private List<Tile> tileList = new LinkedList<Tile>();
    private LargeImageViewer.OnTileChangedListener onTileChangedListener;

    private ObjectPool<Tile> tilePool = new ObjectPool<Tile>(new ObjectPool.ItemFactory<Tile>() {
        @Override
        public Tile newItem() {
            return new Tile();
        }
    }, 60);
    private ObjectPool<Rect> rectPool = new ObjectPool<Rect>(new ObjectPool.ItemFactory<Rect>() {
        @Override
        public Rect newItem() {
            return new Rect();
        }
    });

    public TileManager(Context context, LargeImageViewer largeImageViewer) {
        this.context = context;
        this.largeImageViewer = largeImageViewer;
    }

    public void update(UpdateParams updateParams){
        Rect visibleRect = updateParams.visibleRect;

        // 原始图片的宽高
        int imageWidth = largeImageViewer.getExecutor().getDecoder().getImageWidth();
        int imageHeight = largeImageViewer.getExecutor().getDecoder().getImageHeight();

        // 原始图和预览图对比的缩放比例
        float originWidthScale = (float) imageWidth / updateParams.previewDrawableWidth;
        float originHeightScale = (float) imageHeight / updateParams.previewDrawableHeight;

        // 计算绘制区域时，每边应该增加的量
        int drawWidthAdd = (int) ((float) visibleRect.width() / tiles / 2);
        int drawHeightAdd = (int) ((float) visibleRect.height() / tiles / 2);

        // 将显示区域加大一圈，计算出绘制区域，宽高各增加一个平均值，为的是提前将四周加载出来，用户缓慢滑动的时候可以提前看到四周的图像
        Rect newDrawRect = rectPool.get();
        newDrawRect.left = Math.max(0, visibleRect.left - drawWidthAdd);
        newDrawRect.top = Math.max(0, visibleRect.top - drawHeightAdd);
        newDrawRect.right = Math.min(updateParams.previewDrawableWidth, visibleRect.right + drawWidthAdd);
        newDrawRect.bottom = Math.min(updateParams.previewDrawableHeight, visibleRect.bottom + drawHeightAdd);

        // 计算碎片的尺寸
        int finalTiles = tiles + 1;
        int drawTileWidth = newDrawRect.width() / finalTiles;
        int drawTileHeight = newDrawRect.height() / finalTiles;

        // 根据碎片尺寸修剪drawRect，使其正好能整除碎片
        newDrawRect.right = newDrawRect.left + (finalTiles * drawTileWidth);
        newDrawRect.bottom = newDrawRect.top + (finalTiles * drawTileHeight);

        // 计算显示区域在完整图片中对应的区域，重点是各用各的缩放比例（这很重要），因为宽或高的比例可能不一样
        Rect newSrcRect = new Rect(
                Math.max(0, Math.round(newDrawRect.left * originWidthScale)),
                Math.max(0, Math.round(newDrawRect.top * originHeightScale)),
                Math.min(imageWidth, Math.round(newDrawRect.right * originWidthScale)),
                Math.min(imageHeight, Math.round(newDrawRect.bottom * originHeightScale)));

        // 根据src区域大小计算缩放比例，由于绘制区域比显示区域大了一圈了，因此计算inSampleSize时targetSize也得大一圈
        float targetSizeScale = ((float) tiles / 10) + 1;
        int targetWidth = Math.round(updateParams.imageViewWidth * targetSizeScale);
        int targetHeight = Math.round(updateParams.imageViewHeight * targetSizeScale);
        ImageSizeCalculator imageSizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
        int inSampleSize = imageSizeCalculator.calculateInSampleSize(newSrcRect.width(), newSrcRect.height(), targetWidth, targetHeight, false);

        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, NAME + ". split start" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ", lastScale=" + largeImageViewer.getLastScale() +
                    ", scale=" + largeImageViewer.getScale() +
                    ". new drawRect=" + newDrawRect.toShortString() +
                    ". old drawRect=" + drawRect.toShortString() +
                    ". inSampleSize=" + inSampleSize +
                    ", tiles=" + tileList.size());
        }

        // 哪边可以扩展了就扩大哪边
        boolean needLoad = false;
        if (largeImageViewer.getScale() == largeImageViewer.getLastScale() && !drawRect.isEmpty()) {
            int leftAndRightEdge = Math.round(drawWidthAdd * 0.8f);
            int topAndBottomEdge = Math.round(drawHeightAdd * 0.8f);
            int leftSpace = Math.abs(newDrawRect.left - drawRect.left);
            int topSpace = Math.abs(newDrawRect.top - drawRect.top);
            int rightSpace = Math.abs(newDrawRect.right - drawRect.right);
            int bottomSpace = Math.abs(newDrawRect.bottom - drawRect.bottom);

            Rect finalNewDrawRect = rectPool.get();
            finalNewDrawRect.set(drawRect);
            if (newDrawRect.left < drawRect.left && (leftSpace > leftAndRightEdge || drawRect.left - drawTileWidth <=0)) {
                finalNewDrawRect.left = Math.max(0, drawRect.left - drawTileWidth);
                int newDrawRight = drawRect.right;
                while (newDrawRect.right <= newDrawRight - drawTileWidth) {
                    newDrawRight = newDrawRight - drawTileWidth;
                }
                finalNewDrawRect.right = newDrawRight;

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect left expand. newDrawRect=" + finalNewDrawRect.toShortString());
                }
                needLoad = true;
            }

            if (newDrawRect.top < drawRect.top && (topSpace > topAndBottomEdge || drawRect.top - drawTileHeight <= 0)) {
                finalNewDrawRect.top = Math.max(0, drawRect.top - drawTileHeight);
                int newDrawBottom = drawRect.bottom;
                while (newDrawRect.bottom <= Math.round(newDrawBottom - drawTileHeight)) {
                    newDrawBottom = newDrawBottom - drawTileHeight;
                }
                finalNewDrawRect.bottom = newDrawBottom;

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect top expand. newDrawRect=" + finalNewDrawRect.toShortString());
                }
                needLoad = true;
            }

            if (newDrawRect.right > drawRect.right && (rightSpace > leftAndRightEdge || drawRect.right + drawTileWidth >= updateParams.previewDrawableWidth)) {
                int newDrawLeft = drawRect.left;
                while (newDrawRect.left >= newDrawLeft + drawTileWidth) {
                    newDrawLeft = newDrawLeft + drawTileWidth;
                }
                finalNewDrawRect.left = newDrawLeft;
                finalNewDrawRect.right = Math.min(updateParams.previewDrawableWidth, drawRect.right + drawTileWidth);

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect right expand. newDrawRect=" + finalNewDrawRect.toShortString());
                }
                needLoad = true;
            }

            if (newDrawRect.bottom > drawRect.bottom && (bottomSpace > topAndBottomEdge || drawRect.bottom + drawTileHeight >= updateParams.previewDrawableHeight)) {
                int newDrawTop = drawRect.top;
                while (newDrawRect.top >= newDrawTop + drawTileHeight) {
                    newDrawTop = newDrawTop + drawTileHeight;
                }
                finalNewDrawRect.top = newDrawTop;
                finalNewDrawRect.bottom = Math.min(updateParams.previewDrawableHeight, drawRect.bottom + drawTileHeight);

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect bottom expand. newDrawRect=" + finalNewDrawRect.toShortString());
                }
                needLoad = true;
            }

            newDrawRect.set(finalNewDrawRect);
            finalNewDrawRect.setEmpty();
            rectPool.put(finalNewDrawRect);
        } else {
            needLoad = true;
        }

        // 不需要扩展说明，当前已加载的区域够用，那就结束吧
        if (!needLoad) {
            Log.e(Sketch.TAG, NAME + ". split finished draw rect no change" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ". new drawRect=" + newDrawRect.toShortString() +
                    ". old drawRect=" + drawRect.toShortString() +
                    ", tiles=" + tileList.size());
            return;
        }

        this.drawRect.set(newDrawRect);
        newSrcRect = new Rect(
                Math.max(0, Math.round(newDrawRect.left * originWidthScale)),
                Math.max(0, Math.round(newDrawRect.top * originHeightScale)),
                Math.min(imageWidth, Math.round(newDrawRect.right * originWidthScale)),
                Math.min(imageHeight, Math.round(newDrawRect.bottom * originHeightScale)));
        this.srcRect.set(newSrcRect);

        // 回收那些已经完全不可见的碎片
        Tile tile;
        Iterator<Tile> tileIterator = tileList.iterator();
        while (tileIterator.hasNext()) {
            tile = tileIterator.next();

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以回收这个碎片了
            if (largeImageViewer.getScale() != tile.scale || !SketchUtils.isCross(tile.drawRect, newDrawRect)) {
                if (!tile.isEmpty()) {
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". recycle tile. tile=" + tile.getInfo());
                    }
                    tileIterator.remove();
                    tile.clean();
                    tilePool.put(tile);
                } else {
                    tile.refreshKey();
                    tileIterator.remove();
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". recycle loading tile and refresh key. tile=" + tile.getInfo());
                    }
                }
            }
        }
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". recycle tiles. tiles=" + tileList.size());
        }

        // 找出所有的空白区域，然后一个一个加载
        List<Rect> emptyRectList = findEmptyRect(newDrawRect, tileList);
        if (emptyRectList != null && emptyRectList.size() > 0) {
            for(Rect emptyRect : emptyRectList){
                int tileLeft = emptyRect.left, tileTop = emptyRect.top;
                int tileRight = 0, tileBottom = 0;
                if (Sketch.isDebugMode()) {
                    Log.i(Sketch.TAG, NAME + ". load emptyRect=" + emptyRect.toShortString());
                }
                while (Math.round(tileRight) < emptyRect.right || Math.round(tileBottom) < emptyRect.bottom) {
                    tileRight = Math.min(tileLeft + drawTileWidth, emptyRect.right);
                    tileBottom = Math.min(tileTop + drawTileHeight, emptyRect.bottom);

                    if (canLoad(Math.round(tileLeft), Math.round(tileTop), Math.round(tileRight), Math.round(tileBottom))) {
                        Tile loadTile = tilePool.get();
                        loadTile.drawRect.set(Math.round(tileLeft), Math.round(tileTop), Math.round(tileRight), Math.round(tileBottom));
                        loadTile.srcRect.set(
                                Math.max(0, Math.round(tileLeft * originWidthScale)),
                                Math.max(0, Math.round(tileTop * originHeightScale)),
                                Math.min(imageWidth, Math.round(tileRight * originWidthScale)),
                                Math.min(imageHeight, Math.round(tileBottom * originHeightScale))
                        );
                        loadTile.inSampleSize = inSampleSize;
                        loadTile.scale = largeImageViewer.getScale();

                        // 提交任务
                        loadTile.refreshKey();
                        tileList.add(loadTile);
                        if (Sketch.isDebugMode()) {
                            Log.d(Sketch.TAG, NAME + ". submit and refresh key" +
                                    ". drawRect=" + newDrawRect.toShortString() + ", tile=" + loadTile.getInfo());
                        }
                        largeImageViewer.getExecutor().submit(loadTile.getKey(), loadTile);
                    } else {
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, NAME + ". repeated tile tileDrawRect=" +
                                    Math.round(tileLeft) + ", " + Math.round(tileTop) + ", " +
                                    Math.round(tileRight) + ", " + Math.round(tileBottom));
                        }
                    }

                    if (Math.round(tileRight) >= emptyRect.right) {
                        tileLeft = emptyRect.left;
                        tileTop = tileBottom;
                    } else {
                        tileLeft = tileRight;
                    }
                }
            }
        } else {
            Log.w(Sketch.TAG, NAME + ". not found empty rect");
        }

        if (Sketch.isDebugMode()) {
            Log.e(Sketch.TAG, NAME + ". split finished" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ", drawRect=" + newDrawRect.toShortString() +
                    ", tiles=" + tileList.size());
        }

        if (onTileChangedListener != null) {
            onTileChangedListener.onTileChanged(largeImageViewer);
        }
    }

    private boolean canLoad(int left, int top, int right, int bottom) {
        for (Tile drawTile : tileList) {
            if (drawTile.drawRect.left == left &&
                    drawTile.drawRect.top == top &&
                    drawTile.drawRect.right == right &&
                    drawTile.drawRect.bottom == bottom) {
                return false;
            }
        }

        return true;
    }

    /**
     * 假如有一个矩形，并且已知这个矩形中的N个碎片，那么要找出所有的空白碎片（不可用的碎片会从已知列表中删除）
     * @param rect 那个矩形
     * @param tileList 已知碎片
     * @return 所有空白的碎片
     */
    public List<Rect> findEmptyRect(Rect rect, List<Tile> tileList) {
        if (rect.isEmpty()) {
            return null;
        }

        List<Rect> emptyRectList = null;
        if (tileList == null || tileList.size() == 0) {
            emptyRectList = new LinkedList<Rect>();
            emptyRectList.add(rect);
            return emptyRectList;
        }

        // 按离左上角的距离排序
        Collections.sort(tileList, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                if (o1.drawRect.top >= o2.drawRect.bottom || o2.drawRect.top >= o1.drawRect.bottom) {
                    return o1.drawRect.top - o2.drawRect.top;
                } else {
                    return o1.drawRect.left - o2.drawRect.left;
                }
            }
        });

        int left = rect.left, top = rect.top, right = 0, bottom = -1;
        Tile lastRect = null;
        Tile childRect;
        Iterator<Tile> rectIterator = tileList.iterator();
        while (rectIterator.hasNext()) {
            childRect = rectIterator.next();

            boolean newLine = lastRect == null || (childRect.drawRect.top >= bottom);
            if (newLine) {
                // 首先要处理上一行的最后一个
                if (lastRect != null) {
                    if (lastRect.drawRect.right < rect.right) {
                        Rect rightEmptyRect = new Rect(lastRect.drawRect.right, top, rect.right, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(rightEmptyRect);
                    }
                }

                // 然后要更新top和bottom
                top = bottom != -1 ? bottom : top;
                bottom = childRect.drawRect.bottom;

                // 左边有空隙
                if (childRect.drawRect.left > left) {
                    Rect leftEmptyRect = new Rect(left, childRect.drawRect.top, childRect.drawRect.left, childRect.drawRect.bottom);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(leftEmptyRect);
                }

                // 顶部有空隙
                if (childRect.drawRect.top > top) {
                    Rect topEmptyRect = new Rect(left, top, childRect.drawRect.right, childRect.drawRect.top);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(topEmptyRect);
                }

                right = childRect.drawRect.right;
                lastRect = childRect;
            } else {
                boolean available = childRect.drawRect.bottom == lastRect.drawRect.bottom;
                if (available) {
                    // 左边有空隙
                    if (childRect.drawRect.left > right) {
                        Rect leftEmptyRect = new Rect(right, top, childRect.drawRect.left, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(leftEmptyRect);
                    }

                    // 顶部有空隙
                    if (childRect.drawRect.top > top) {
                        Rect topEmptyRect = new Rect(childRect.drawRect.left, top, childRect.drawRect.right, childRect.drawRect.top);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(topEmptyRect);
                    }

                    right = childRect.drawRect.right;
                    lastRect = childRect;
                } else {
                    rectIterator.remove();
                }
            }
        }

        // 最后的结尾处理
        if (right < rect.right) {
            Rect rightEmptyRect = new Rect(right, top, rect.right, bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(rightEmptyRect);
        }

        if (bottom < rect.bottom) {
            Rect bottomEmptyRect = new Rect(rect.left, bottom, rect.right, rect.bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(bottomEmptyRect);
        }

        return emptyRectList;
    }

    public void decodeCompleted(Tile tile, Bitmap bitmap) {
        if (Sketch.isDebugMode()) {
            String bitmapConfig = bitmap.getConfig() != null ? bitmap.getConfig().name() : null;
            Log.i(Sketch.TAG, NAME + ". decodeCompleted" +
                    ". tile=" + tile.getInfo() +
                    ", bitmap=" + bitmap.getWidth() + "x" + bitmap.getHeight() + "(" + bitmapConfig + ")" +
                    ", tiles=" + tileList.size());
        }

        tile.bitmap = bitmap;
        tile.bitmapDrawSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());

        largeImageViewer.invalidateView();

        if (onTileChangedListener != null) {
            onTileChangedListener.onTileChanged(largeImageViewer);
        }
    }

    public void decodeFailed(Tile tile, DecodeHandler.DecodeFailedException exception) {
        tileList.remove(tile);

        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, NAME + ". decodeFailed. " + exception.getCauseMessage() + ". tile=" + tile.getInfo() + ", tiles=" + tileList.size());
        }

        tile.clean();
        tilePool.put(tile);
    }

    public void clean(String why){
        for (Tile tile : tileList) {
            tile.refreshKey();
            tile.clean();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". clean tile and refresh key. " + why + ". tile=" + tile.getInfo());
            }
        }
        tileList.clear();
    }

    public void recycle(@SuppressWarnings("UnusedParameters") String why){
        tilePool.clear();
        rectPool.clear();
    }

    @SuppressWarnings("unused")
    public Rect getDrawRect() {
        return drawRect;
    }

    public Rect getSrcRect() {
        return srcRect;
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    @SuppressWarnings("unused")
    public int getTiles() {
        return tiles;
    }

    @SuppressWarnings("unused")
    public void setTiles(int tiles) {
        if (tiles > 0) {
            this.tiles = tiles;
        }
    }

    @SuppressWarnings("unused")
    public void setOnTileChangedListener(LargeImageViewer.OnTileChangedListener onTileChangedListener) {
        this.onTileChangedListener = onTileChangedListener;
    }
}