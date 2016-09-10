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
// TODO: 16/9/4 缩放过程中不解码
public class TileManager {
    private static final String NAME = "TileManager";

    private Context context;
    private LargeImageViewer largeImageViewer;

    private int tiles = 3;
    private Rect lastOriginDrawRect = new Rect();
    private Rect lastOriginSrcRect = new Rect();
    private Rect lastRealDrawRect = new Rect();
    private Rect lastRealSrcRect = new Rect();

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

    public void update(Rect visibleRect, int viewWidth, int viewHeight,
                       int imageWidth, int imageHeight,
                       int previewImageWidth, int previewImageHeight) {
        // 原始图和预览图对比的缩放比例
        float originWidthScale = (float) imageWidth / previewImageWidth;
        float originHeightScale = (float) imageHeight / previewImageHeight;

        // 计算绘制区域时，每边应该增加的量
        int drawWidthAdd = (int) ((float) visibleRect.width() / tiles / 2);
        int drawHeightAdd = (int) ((float) visibleRect.height() / tiles / 2);

        // 将显示区域加大一圈，计算出绘制区域，宽高各增加一个平均值
        // 为的是提前将四周加载出来，用户缓慢滑动的时候可以提前看到四周的图像
        Rect newDrawRect = rectPool.get();
        newDrawRect.left = Math.max(0, visibleRect.left - drawWidthAdd);
        newDrawRect.top = Math.max(0, visibleRect.top - drawHeightAdd);
        newDrawRect.right = Math.min(previewImageWidth, visibleRect.right + drawWidthAdd);
        newDrawRect.bottom = Math.min(previewImageHeight, visibleRect.bottom + drawHeightAdd);

        // 计算碎片的尺寸
        int finalTiles = tiles + 1;
        int tileWidth = newDrawRect.width() / finalTiles;
        int tileHeight = newDrawRect.height() / finalTiles;

        // 根据碎片尺寸修剪drawRect，使其正好能整除碎片
        if (newDrawRect.right < previewImageWidth) {
            newDrawRect.right = newDrawRect.left + (finalTiles * tileWidth);
        } else if (newDrawRect.left > 0) {
            newDrawRect.left = newDrawRect.right - (finalTiles * tileWidth);
        }
        if (newDrawRect.bottom < previewImageHeight) {
            newDrawRect.bottom = newDrawRect.top + (finalTiles * tileHeight);
        } else if (newDrawRect.top > 0) {
            newDrawRect.top = newDrawRect.bottom - (finalTiles * tileHeight);
        }
        lastOriginDrawRect.set(newDrawRect);

        Rect newSrcRect = rectPool.get();
        calculateSrcRect(newSrcRect, newDrawRect, imageWidth, imageHeight, originWidthScale, originHeightScale);
        lastOriginSrcRect.set(newSrcRect);
        int inSampleSize = calculateInSampleSize(newSrcRect.width(), newSrcRect.height(), viewWidth, viewHeight);

        newSrcRect.setEmpty();
        rectPool.put(newSrcRect);
        //noinspection UnusedAssignment
        newSrcRect = null;

        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, NAME + ". update start" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ", newDrawRect=" + newDrawRect.toShortString() +
                    ", lastDrawRect=" + lastRealDrawRect.toShortString() +
                    ", inSampleSize=" + inSampleSize +
                    ", lastScale=" + largeImageViewer.getLastZoomScale() +
                    ", scale=" + largeImageViewer.getZoomScale() +
                    ", tiles=" + tileList.size());
        }

        // 根据上一次绘制区域的和新绘制区域的差异计算出最终的绘制区域
        Rect finalDrawRect = rectPool.get();
        calculateTilesDrawRect(finalDrawRect, newDrawRect, drawWidthAdd, drawHeightAdd,
                tileWidth, tileHeight, previewImageWidth, previewImageHeight);

        newDrawRect.setEmpty();
        rectPool.put(newDrawRect);
        //noinspection UnusedAssignment
        newDrawRect = null;

        if (!finalDrawRect.isEmpty()) {
            // 如果最终绘制区域跟上一次没有变化就不继续了
            if (!finalDrawRect.equals(lastRealDrawRect)) {
                this.lastRealDrawRect.set(finalDrawRect);
                calculateSrcRect(this.lastRealSrcRect, finalDrawRect, imageWidth, imageHeight,
                        originWidthScale, originHeightScale);

                // 回收那些已经超出绘制区域的碎片
                recycleTiles(tileList, finalDrawRect);

                // 找出所有的空白区域，然后一个一个加载
                List<Rect> emptyRectList = findEmptyRect(finalDrawRect, tileList);
                if (emptyRectList != null && emptyRectList.size() > 0) {
                    loadTiles(emptyRectList, tileWidth, tileHeight, imageWidth, imageHeight,
                            originWidthScale, originHeightScale, inSampleSize, finalDrawRect);
                } else {
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". not found empty rect");
                    }
                }

                if (onTileChangedListener != null) {
                    onTileChangedListener.onTileChanged(largeImageViewer);
                }

                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, NAME + ". update finished" +
                            ", drawRect=" + finalDrawRect.toShortString() +
                            ", tiles=" + tileList.size());
                }
            } else {
                if (Sketch.isDebugMode()) {
                    Log.e(Sketch.TAG, NAME + ". update finished draw rect no change");
                }
            }
        } else {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, NAME + ". update finished. final draw rect is empty. finalDrawRect=" + finalDrawRect.toShortString());
            }
        }

        finalDrawRect.setEmpty();
        rectPool.put(finalDrawRect);
        //noinspection UnusedAssignment
        finalDrawRect = null;
    }

    /**
     * 计算绘制区域在完整图片中对应的区域，重点是各用各的缩放比例（这很重要），因为宽或高的比例可能不一样
     */
    private void calculateSrcRect(Rect srcRect, Rect drawRect, int imageWidth, int imageHeight,
                                  float originWidthScale, float originHeightScale) {
        srcRect.left = Math.max(0, Math.round(drawRect.left * originWidthScale));
        srcRect.top = Math.max(0, Math.round(drawRect.top * originHeightScale));
        srcRect.right = Math.min(imageWidth, Math.round(drawRect.right * originWidthScale));
        srcRect.bottom = Math.min(imageHeight, Math.round(drawRect.bottom * originHeightScale));
    }

    /**
     * 计算解码时的缩放比例
     */
    private int calculateInSampleSize(int srcWidth, int srcHeight, int viewWidth, int viewHeight) {


        // 由于绘制区域比显示区域大了一圈，因此targetSize也得大一圈
        float targetSizeScale = ((float) tiles / 10) + 1;
        int targetWidth = Math.round(viewWidth * targetSizeScale);
        int targetHeight = Math.round(viewHeight * targetSizeScale);

        ImageSizeCalculator imageSizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();

        return imageSizeCalculator.calculateInSampleSize(srcWidth, srcHeight, targetWidth, targetHeight, false);
    }

    /**
     * 在上一个绘制区域的基础上计算出根据新的绘制区域，计算出最终的绘制区域
     */
    private void calculateTilesDrawRect(Rect finalDrawRect, Rect newDrawRect,
                                        int drawWidthAdd, int drawHeightAdd,
                                        int drawTileWidth, int drawTileHeight,
                                        int maxDrawWidth, int maxDrawHeight) {
        // 缩放比例已改变或者这是第一次就直接用新的绘制区域
        if (largeImageViewer.getZoomScale() != largeImageViewer.getLastZoomScale() || lastRealDrawRect.isEmpty()) {
            finalDrawRect.set(newDrawRect);
            return;
        }

        // 以上一次的绘制区域为基础
        finalDrawRect.set(lastRealDrawRect);

        int leftAndRightEdge = Math.round(drawWidthAdd * 0.8f);
        int topAndBottomEdge = Math.round(drawHeightAdd * 0.8f);
        int leftSpace = Math.abs(newDrawRect.left - finalDrawRect.left);
        int topSpace = Math.abs(newDrawRect.top - finalDrawRect.top);
        int rightSpace = Math.abs(newDrawRect.right - finalDrawRect.right);
        int bottomSpace = Math.abs(newDrawRect.bottom - finalDrawRect.bottom);

        // 左边需要加一列
        if (newDrawRect.left < finalDrawRect.left) {
            if (newDrawRect.left == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                finalDrawRect.left = 0;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect left to 0" + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            } else if (leftSpace > leftAndRightEdge || finalDrawRect.left - drawTileWidth <= 0) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (finalDrawRect.left > newDrawRect.left) {
                    finalDrawRect.left = Math.max(0, finalDrawRect.left - drawTileWidth);
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". final draw rect left expand " + drawTileWidth + ", finalDrawRect=" + finalDrawRect.toShortString());
                    }
                }
            }
        }

        // 顶部需要加一行
        if (newDrawRect.top < finalDrawRect.top) {
            if (newDrawRect.top == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                finalDrawRect.top = 0;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect top to 0" + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            } else if (topSpace > topAndBottomEdge || finalDrawRect.top - drawTileHeight <= 0) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (finalDrawRect.top > newDrawRect.top) {
                    finalDrawRect.top = Math.max(0, finalDrawRect.top - drawTileHeight);
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". final draw rect top expand " + drawTileHeight + ", finalDrawRect=" + finalDrawRect.toShortString());
                    }
                }
            }
        }


        // 右边需要加一列
        if (newDrawRect.right > finalDrawRect.right) {
            if (newDrawRect.right == maxDrawWidth) {
                // 如果已经到边了，管它还差多少，直接顶到边
                finalDrawRect.right = maxDrawWidth;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect right to " + maxDrawWidth + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            } else if (rightSpace > leftAndRightEdge || finalDrawRect.right + drawTileWidth >= maxDrawWidth) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (finalDrawRect.right < newDrawRect.right) {
                    finalDrawRect.right = Math.min(maxDrawWidth, finalDrawRect.right + drawTileWidth);
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". final draw rect right expand " + drawTileWidth + ", finalDrawRect=" + finalDrawRect.toShortString());
                    }
                }
            }
        }

        // 底部需要加一行
        if (newDrawRect.bottom > finalDrawRect.bottom) {
            if (newDrawRect.bottom > maxDrawHeight) {
                // 如果已经到边了，管它还差多少，直接顶到边
                finalDrawRect.bottom = maxDrawHeight;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect bottom to " + maxDrawHeight + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            } else if (bottomSpace > topAndBottomEdge || finalDrawRect.bottom + drawTileHeight >= maxDrawHeight) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (finalDrawRect.bottom < newDrawRect.bottom) {
                    finalDrawRect.bottom = Math.min(maxDrawHeight, finalDrawRect.bottom + drawTileHeight);
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". final draw rect bottom expand " + drawTileHeight + ", finalDrawRect=" + finalDrawRect.toShortString());
                    }
                }
            }
        }

        // 前面把四周给加大了一圈，这里要把多余的剪掉
        while (finalDrawRect.left + drawTileWidth < newDrawRect.left ||
                finalDrawRect.top + drawTileHeight < newDrawRect.top ||
                finalDrawRect.right - drawTileWidth > newDrawRect.right ||
                finalDrawRect.bottom - drawTileHeight > newDrawRect.bottom) {
            if (finalDrawRect.left + drawTileWidth < newDrawRect.left) {
                finalDrawRect.left += drawTileWidth;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect left reduced " + drawTileWidth + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            }
            if (finalDrawRect.top + drawTileHeight < newDrawRect.top) {
                finalDrawRect.top += drawTileHeight;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect top reduced " + drawTileHeight + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            }
            if (finalDrawRect.right - drawTileWidth > newDrawRect.right) {
                finalDrawRect.right -= drawTileWidth;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect right reduced " + drawTileWidth + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            }
            if (finalDrawRect.bottom - drawTileHeight > newDrawRect.bottom) {
                finalDrawRect.bottom -= drawTileHeight;
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". final draw rect bottom reduced " + drawTileHeight + ", finalDrawRect=" + finalDrawRect.toShortString());
                }
            }
        }
    }

    /**
     * 去重
     */
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
     *
     * @param rect     那个矩形
     * @param tileList 已知碎片
     * @return 所有空白的碎片
     */
    public List<Rect> findEmptyRect(Rect rect, List<Tile> tileList) {
        if (rect.isEmpty()) {
            return null;
        }

        List<Rect> emptyRectList = null;
        if (tileList == null || tileList.size() == 0) {
            Rect fullRect = rectPool.get();
            fullRect.set(rect);

            emptyRectList = new LinkedList<Rect>();
            emptyRectList.add(fullRect);
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
                        Rect rightEmptyRect = rectPool.get();
                        rightEmptyRect.set(lastRect.drawRect.right, top, rect.right, bottom);
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
                    Rect leftEmptyRect = rectPool.get();
                    leftEmptyRect.set(left, childRect.drawRect.top, childRect.drawRect.left, childRect.drawRect.bottom);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(leftEmptyRect);
                }

                // 顶部有空隙
                if (childRect.drawRect.top > top) {
                    Rect topEmptyRect = rectPool.get();
                    topEmptyRect.set(left, top, childRect.drawRect.right, childRect.drawRect.top);
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
                        Rect leftEmptyRect = rectPool.get();
                        leftEmptyRect.set(right, top, childRect.drawRect.left, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(leftEmptyRect);
                    }

                    // 顶部有空隙
                    if (childRect.drawRect.top > top) {
                        Rect topEmptyRect = rectPool.get();
                        topEmptyRect.set(childRect.drawRect.left, top, childRect.drawRect.right, childRect.drawRect.top);
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
            Rect rightEmptyRect = rectPool.get();
            rightEmptyRect.set(right, top, rect.right, bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(rightEmptyRect);
        }

        if (bottom < rect.bottom) {
            Rect bottomEmptyRect = rectPool.get();
            bottomEmptyRect.set(rect.left, bottom, rect.right, rect.bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(bottomEmptyRect);
        }

        return emptyRectList;
    }

    /**
     * 回收哪些已经超出绘制区域的碎片
     */
    private void recycleTiles(List<Tile> tileList, Rect drawRect) {
        Tile tile;
        Iterator<Tile> tileIterator = tileList.iterator();
        while (tileIterator.hasNext()) {
            tile = tileIterator.next();

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以回收这个碎片了
            if (largeImageViewer.getZoomScale() != tile.scale || !SketchUtils.isCross(tile.drawRect, drawRect)) {
                if (!tile.isEmpty()) {
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". recycle tile. tile=" + tile.getInfo());
                    }
                    tileIterator.remove();
                    tile.clean();
                    tilePool.put(tile);
                } else {
                    if (Sketch.isDebugMode()) {
                        Log.w(Sketch.TAG, NAME + ". recycle loading tile and refresh key. tile=" + tile.getInfo());
                    }
                    tile.refreshKey();
                    tileIterator.remove();
                }
            }
        }
    }

    private void loadTiles(List<Rect> emptyRectList, int tileWidth, int tileHeight,
                           int imageWidth, int imageHeight, float originWidthScale, float originHeightScale,
                           int inSampleSize, Rect finalDrawRect) {
        for (Rect emptyRect : emptyRectList) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". load emptyRect=" + emptyRect.toShortString());
            }

            int tileLeft = emptyRect.left, tileTop = emptyRect.top, tileRight = 0, tileBottom = 0;
            while (Math.round(tileRight) < emptyRect.right || Math.round(tileBottom) < emptyRect.bottom) {
                tileRight = Math.min(tileLeft + tileWidth, emptyRect.right);
                tileBottom = Math.min(tileTop + tileHeight, emptyRect.bottom);

                if (canLoad(tileLeft, tileTop, tileRight, tileBottom)) {
                    Tile loadTile = tilePool.get();

                    loadTile.drawRect.set(tileLeft, tileTop, tileRight, tileBottom);
                    loadTile.inSampleSize = inSampleSize;
                    loadTile.scale = largeImageViewer.getZoomScale();
                    calculateSrcRect(loadTile.srcRect, loadTile.drawRect, imageWidth, imageHeight, originWidthScale, originHeightScale);

                    tileList.add(loadTile);
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". submit and refresh key" +
                                ". drawRect=" + finalDrawRect.toShortString() + ", tile=" + loadTile.getInfo());
                    }

                    loadTile.refreshKey();
                    largeImageViewer.getExecutor().submit(loadTile.getKey(), loadTile);
                } else {
                    if (Sketch.isDebugMode()) {
                        Log.w(Sketch.TAG, NAME + ". repeated tile. tileDrawRect=" +
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

            emptyRect.setEmpty();
            rectPool.put(emptyRect);
        }
    }

    public void decodeCompleted(Tile tile, Bitmap bitmap) {
        if (Sketch.isDebugMode()) {
            String bitmapConfig = bitmap.getConfig() != null ? bitmap.getConfig().name() : null;
            Log.i(Sketch.TAG, NAME + ". decode completed" +
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
            Log.w(Sketch.TAG, NAME + ". decode failed. " + exception.getCauseMessage() + "" +
                    ". tile=" + tile.getInfo() + "" +
                    ", tiles=" + tileList.size());
        }

        tile.clean();
        tilePool.put(tile);
    }

    public void clean(String why) {
        for (Tile tile : tileList) {
            tile.refreshKey();
            tile.clean();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". clean tile and refresh key. " + why + ". tile=" + tile.getInfo());
            }
        }
        tileList.clear();
    }

    public void recycle(@SuppressWarnings("UnusedParameters") String why) {
        tilePool.clear();
        rectPool.clear();
    }

    @SuppressWarnings("unused")
    public Rect getOriginDrawRect() {
        return lastOriginDrawRect;
    }

    public Rect getOriginSrcRect() {
        return lastOriginSrcRect;
    }

    @SuppressWarnings("unused")
    public Rect getRealDrawRect() {
        return lastRealDrawRect;
    }

    public Rect getRealSrcRect() {
        return lastRealSrcRect;
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

    public long getBytes() {
        if (tileList == null || tileList.size() <= 0) {
            return 0;
        }

        long bytes = 0;
        for (Tile tile : tileList) {
            if (!tile.isEmpty()) {
                bytes += SketchUtils.getBitmapByteCount(tile.bitmap);
            }
        }
        return bytes;
    }
}