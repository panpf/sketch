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
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.util.ObjectPool;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 碎片管理器
 */
// TODO: 2016/12/17 优化碎片计算规则，尽量保证每块碎片的尺寸都是一样的，这样就能充分利用inBitmap功能减少内存分配提高流畅度
class TileManager {
    private static final String NAME = "TileManager";
    int tiles = 3;  // 碎片基数，例如碎片基数是3时，就将绘制区域分割成一个(3+1)x(3+1)=16个方块
    Rect visibleRect = new Rect();  // 可见区域，当前用户真正能看见的区域
    Rect drawRect = new Rect(); // 绘制区域，可见区域加大一圈就是绘制区域，为的是提前将四周加载出来，用户缓慢滑动时可直接看到
    Rect decodeRect = new Rect();   // 解码区域，真正需要解码的区域，是以绘制区域为基础，滑动时哪边不够了就在扩展哪边，解码区域一定比绘制区域大
    Rect drawSrcRect = new Rect();
    Rect decodeSrcRect = new Rect();
    List<Tile> tileList = new LinkedList<Tile>();
    LargeImageViewer.OnTileChangedListener onTileChangedListener;
    private Context context;
    private BitmapPool bitmapPool;
    private LargeImageViewer largeImageViewer;
    private ObjectPool<Tile> tilePool = new ObjectPool<Tile>(new ObjectPool.ObjectFactory<Tile>() {
        @Override
        public Tile newObject() {
            return new Tile();
        }
    }, 60);
    private ObjectPool<Rect> rectPool = new ObjectPool<Rect>(new ObjectPool.ObjectFactory<Rect>() {
        @Override
        public Rect newObject() {
            return new Rect();
        }
    }, 20);

    TileManager(Context context, LargeImageViewer largeImageViewer) {
        context = context.getApplicationContext();
        this.context = context;
        this.bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        this.largeImageViewer = largeImageViewer;
    }

    void update(Rect newVisibleRect, Point previewDrawableSize, Point imageViewSize, Point imageSize, boolean zooming) {
        if (zooming) {
            SLog.w(SLogType.LARGE, NAME, "zooming. newVisibleRect=%s, tiles=%d",
                    newVisibleRect.toShortString(), tileList.size());
            return;
        }

        // 过滤掉重复的刷新
        if (visibleRect.equals(newVisibleRect)) {
            SLog.w(SLogType.LARGE, NAME, "visible rect no changed. update. newVisibleRect=%s, oldVisibleRect=%s",
                    newVisibleRect.toShortString(), visibleRect.toShortString());
            return;
        }
        visibleRect.set(newVisibleRect);

        final int viewWidth = imageViewSize.x;
        final int viewHeight = imageViewSize.y;
        final int previewImageWidth = previewDrawableSize.x;
        final int previewImageHeight = previewDrawableSize.y;
        final int imageWidth = imageSize.x;
        final int imageHeight = imageSize.y;

        // 原始图和预览图对比的缩放比例
        final float originWidthScale = (float) imageWidth / previewImageWidth;
        final float originHeightScale = (float) imageHeight / previewImageHeight;

        // 计算绘制区域时，每边应该增加的量
        final int drawWidthAdd = (int) ((float) newVisibleRect.width() / tiles / 2);
        final int drawHeightAdd = (int) ((float) newVisibleRect.height() / tiles / 2);

        // 将显示区域加大一圈，计算出绘制区域，宽高各增加一个平均值
        // 为的是提前将四周加载出来，用户缓慢滑动的时候可以提前看到四周的图像
        Rect newDrawRect = rectPool.get();
        newDrawRect.left = Math.max(0, newVisibleRect.left - drawWidthAdd);
        newDrawRect.top = Math.max(0, newVisibleRect.top - drawHeightAdd);
        newDrawRect.right = Math.min(previewImageWidth, newVisibleRect.right + drawWidthAdd);
        newDrawRect.bottom = Math.min(previewImageHeight, newVisibleRect.bottom + drawHeightAdd);

        if (newDrawRect.isEmpty()) {
            SLog.e(SLogType.LARGE, NAME, "newDrawRect is empty. %s", newDrawRect.toShortString());
            return;
        }

        // 计算碎片的尺寸
        final int finalTiles = tiles + 1;
        final int tileWidth = newDrawRect.width() / finalTiles;
        final int tileHeight = newDrawRect.height() / finalTiles;

        if (tileWidth <= 0 || tileHeight <= 0) {
            SLog.e(SLogType.LARGE, NAME, "tileWidth or tileHeight exception. %dx%d", tileWidth, tileHeight);
            return;
        }

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

        Rect newDrawSrcRect = rectPool.get();
        calculateSrcRect(newDrawSrcRect, newDrawRect, imageWidth, imageHeight, originWidthScale, originHeightScale);
        int inSampleSize = calculateInSampleSize(newDrawSrcRect.width(), newDrawSrcRect.height(), viewWidth, viewHeight);

        SLog.i(SLogType.LARGE, NAME, "update start. newVisibleRect=%s, newDrawRect=%s, oldDecodeRect=%s, inSampleSize=%d, scale=%s, lastScale=%s, tiles=%d",
                newVisibleRect.toShortString(), newDrawRect.toShortString(), decodeRect.toShortString(),
                inSampleSize, largeImageViewer.getZoomScale(), largeImageViewer.getLastZoomScale(), tileList.size());

        // 根据上一次绘制区域的和新绘制区域的差异计算出最终的绘制区域
        Rect newDecodeRect = rectPool.get();
        calculateTilesDecodeRect(newDecodeRect, newDrawRect, drawWidthAdd, drawHeightAdd,
                tileWidth, tileHeight, previewImageWidth, previewImageHeight);

        Rect newDecodeSrcRect = rectPool.get();
        calculateSrcRect(newDecodeSrcRect, newDecodeRect, imageWidth, imageHeight,
                originWidthScale, originHeightScale);

        if (!newDecodeRect.isEmpty()) {
            // 如果最终绘制区域跟上一次没有变化就不继续了
            if (!newDecodeRect.equals(decodeRect)) {

                // 回收那些已经超出绘制区域的碎片
                recycleTiles(tileList, newDecodeRect);

                // 找出所有的空白区域，然后一个一个加载
                List<Rect> emptyRectList = findEmptyRect(newDecodeRect, tileList);
                if (emptyRectList != null && emptyRectList.size() > 0) {
                    loadTiles(emptyRectList, tileWidth, tileHeight, imageWidth, imageHeight,
                            originWidthScale, originHeightScale, inSampleSize, newDecodeRect);
                } else {
                    SLog.d(SLogType.LARGE, NAME, "not found empty rect");
                }

                if (onTileChangedListener != null) {
                    onTileChangedListener.onTileChanged(largeImageViewer);
                }

                SLog.e(SLogType.LARGE, NAME, "update finished, newDecodeRect=%s, tiles=%d",
                        newDecodeRect.toShortString(), tileList.size());
            } else {
                SLog.e(SLogType.LARGE, NAME, "update finished draw rect no change");
            }
        } else {
            SLog.e(SLogType.LARGE, NAME, "update finished. final draw rect is empty. newDecodeRect=%s",
                    newDecodeRect.toShortString());
        }

        drawRect.set(newDrawRect);
        drawSrcRect.set(newDrawSrcRect);
        decodeRect.set(newDecodeRect);
        decodeSrcRect.set(newDecodeSrcRect);

        newDrawRect.setEmpty();
        newDrawSrcRect.setEmpty();
        newDecodeRect.setEmpty();
        newDecodeSrcRect.setEmpty();

        rectPool.put(newDrawRect);
        rectPool.put(newDrawSrcRect);
        rectPool.put(newDecodeRect);
        rectPool.put(newDecodeSrcRect);
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
    private void calculateTilesDecodeRect(Rect newDecodeRect, Rect newDrawRect,
                                          int drawWidthAdd, int drawHeightAdd,
                                          int drawTileWidth, int drawTileHeight,
                                          int maxDrawWidth, int maxDrawHeight) {
        // 缩放比例已改变或者这是第一次就直接用新的绘制区域
        if (largeImageViewer.getZoomScale() != largeImageViewer.getLastZoomScale() || decodeRect.isEmpty()) {
            newDecodeRect.set(newDrawRect);
            return;
        }

        // 以上一次的绘制区域为基础
        newDecodeRect.set(decodeRect);

        int leftAndRightEdge = Math.round(drawWidthAdd * 0.8f);
        int topAndBottomEdge = Math.round(drawHeightAdd * 0.8f);
        int leftSpace = Math.abs(newDrawRect.left - newDecodeRect.left);
        int topSpace = Math.abs(newDrawRect.top - newDecodeRect.top);
        int rightSpace = Math.abs(newDrawRect.right - newDecodeRect.right);
        int bottomSpace = Math.abs(newDrawRect.bottom - newDecodeRect.bottom);

        // 左边需要加一列
        if (newDrawRect.left < newDecodeRect.left) {
            if (newDrawRect.left == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.left = 0;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect left to 0, newDecodeRect=%s", newDecodeRect.toShortString());
                }
            } else if (leftSpace > leftAndRightEdge || newDecodeRect.left - drawTileWidth <= 0) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (newDecodeRect.left > newDrawRect.left) {
                    newDecodeRect.left = Math.max(0, newDecodeRect.left - drawTileWidth);
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.d(SLogType.LARGE, NAME, "decode rect left expand %d, newDecodeRect=%s",
                                drawTileWidth, newDecodeRect.toShortString());
                    }
                }
            }
        }

        // 顶部需要加一行
        if (newDrawRect.top < newDecodeRect.top) {
            if (newDrawRect.top == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.top = 0;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect top to 0, newDecodeRect=%s", newDecodeRect.toShortString());
                }
            } else if (topSpace > topAndBottomEdge || newDecodeRect.top - drawTileHeight <= 0) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (newDecodeRect.top > newDrawRect.top) {
                    newDecodeRect.top = Math.max(0, newDecodeRect.top - drawTileHeight);
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.d(SLogType.LARGE, NAME, "decode rect top expand %d, newDecodeRect=%s",
                                drawTileHeight, newDecodeRect.toShortString());
                    }
                }
            }
        }


        // 右边需要加一列
        if (newDrawRect.right > newDecodeRect.right) {
            if (newDrawRect.right == maxDrawWidth) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.right = maxDrawWidth;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect right to %d, newDecodeRect=%s",
                            maxDrawWidth, newDecodeRect.toShortString());
                }
            } else if (rightSpace > leftAndRightEdge || newDecodeRect.right + drawTileWidth >= maxDrawWidth) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (newDecodeRect.right < newDrawRect.right) {
                    newDecodeRect.right = Math.min(maxDrawWidth, newDecodeRect.right + drawTileWidth);
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.d(SLogType.LARGE, NAME, "decode rect right expand %d, newDecodeRect=%s",
                                drawTileWidth, newDecodeRect.toShortString());
                    }
                }
            }
        }

        // 底部需要加一行
        if (newDrawRect.bottom > newDecodeRect.bottom) {
            if (newDrawRect.bottom > maxDrawHeight) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.bottom = maxDrawHeight;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect bottom to %d, newDecodeRect=%s",
                            maxDrawHeight, newDecodeRect.toShortString());
                }
            } else if (bottomSpace > topAndBottomEdge || newDecodeRect.bottom + drawTileHeight >= maxDrawHeight) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (newDecodeRect.bottom < newDrawRect.bottom) {
                    newDecodeRect.bottom = Math.min(maxDrawHeight, newDecodeRect.bottom + drawTileHeight);
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.d(SLogType.LARGE, NAME, "decode rect bottom expand %d, newDecodeRect=%s",
                                drawTileHeight, newDecodeRect.toShortString());
                    }
                }
            }
        }

        // 前面把四周给加大了一圈，这里要把多余的剪掉
        while (newDecodeRect.left + drawTileWidth < newDrawRect.left ||
                newDecodeRect.top + drawTileHeight < newDrawRect.top ||
                newDecodeRect.right - drawTileWidth > newDrawRect.right ||
                newDecodeRect.bottom - drawTileHeight > newDrawRect.bottom) {
            if (newDecodeRect.left + drawTileWidth < newDrawRect.left) {
                newDecodeRect.left += drawTileWidth;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect left reduced %d, newDecodeRect=%s",
                            drawTileWidth, newDecodeRect.toShortString());
                }
            }
            if (newDecodeRect.top + drawTileHeight < newDrawRect.top) {
                newDecodeRect.top += drawTileHeight;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect top reduced %d, newDecodeRect=%s",
                            drawTileHeight, newDecodeRect.toShortString());
                }
            }
            if (newDecodeRect.right - drawTileWidth > newDrawRect.right) {
                newDecodeRect.right -= drawTileWidth;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect right reduced %d, newDecodeRect=%s",
                            drawTileWidth, newDecodeRect.toShortString());
                }
            }
            if (newDecodeRect.bottom - drawTileHeight > newDrawRect.bottom) {
                newDecodeRect.bottom -= drawTileHeight;
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "decode rect bottom reduced %d, newDecodeRect=%s",
                            drawTileHeight, newDecodeRect.toShortString());
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
    private List<Rect> findEmptyRect(Rect rect, List<Tile> tileList) {
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
        Comparator<Tile> tileComparator = new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                // 同一行比较left，不同行比较top
                if ((o1.drawRect.top <= o2.drawRect.top && o1.drawRect.bottom >= o2.drawRect.bottom)
                        || (o1.drawRect.top >= o2.drawRect.top && o1.drawRect.bottom <= o2.drawRect.bottom)) {
                    return o1.drawRect.left - o2.drawRect.left;
                } else {
                    return o1.drawRect.top - o2.drawRect.top;
                }
            }
        };
        try {
            Collections.sort(tileList, tileComparator);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            /**
             * Java7的排序算法在检测到A>B, B>C, 但是A<=C的时候就会抛出异常，这里的处理办法是暂时改用旧版的排序算法再次排序
             */

            Configuration configuration = Sketch.with(context).getConfiguration();
            SketchMonitor sketchMonitor = configuration.getMonitor();
            sketchMonitor.onTileSortError(e, tileList, false);

            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            try {
                Collections.sort(tileList, tileComparator);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();

                sketchMonitor.onTileSortError(e, tileList, true);
            }
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "false");
        }

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
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.d(SLogType.LARGE, NAME, "recycle tile. tile=%s", tile.getInfo());
                    }
                    tileIterator.remove();
                    tile.clean(bitmapPool);
                    tilePool.put(tile);
                } else {
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.w(SLogType.LARGE, NAME, "recycle loading tile and refresh key. tile=%s", tile.getInfo());
                    }
                    tile.refreshKey();
                    tileIterator.remove();
                }
            }
        }
    }

    private void loadTiles(List<Rect> emptyRectList, int tileWidth, int tileHeight,
                           int imageWidth, int imageHeight, float originWidthScale, float originHeightScale,
                           int inSampleSize, Rect newDecodeRect) {
        for (Rect emptyRect : emptyRectList) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.d(SLogType.LARGE, NAME, "load emptyRect=%s", emptyRect.toShortString());
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
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.d(SLogType.LARGE, NAME, "submit and refresh key. newDecodeRect=%s, tile=%s",
                                newDecodeRect.toShortString(), loadTile.getInfo());
                    }

                    loadTile.refreshKey();
                    largeImageViewer.getTileDecoder().decodeTile(loadTile);
                } else {
                    if (SLogType.LARGE.isEnabled()) {
                        SLog.w(SLogType.LARGE, NAME, "repeated tile. tileDrawRect=%d, %d, %d, %d",
                                Math.round(tileLeft), Math.round(tileTop), Math.round(tileRight), Math.round(tileBottom));
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

    void decodeCompleted(Tile tile, Bitmap bitmap, int useTime) {
        if (SLogType.LARGE.isEnabled()) {
            String bitmapConfig = bitmap.getConfig() != null ? bitmap.getConfig().name() : null;
            SLog.i(SLogType.LARGE, NAME, "decode completed. useTime=%dms, tile=%s, bitmap=%dx%d(%s), tiles=%d",
                    useTime, tile.getInfo(), bitmap.getWidth(), bitmap.getHeight(), bitmapConfig, tileList.size());
        }

        tile.bitmap = bitmap;
        tile.bitmapDrawSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        tile.decoder = null;

        largeImageViewer.invalidateView();

        if (onTileChangedListener != null) {
            onTileChangedListener.onTileChanged(largeImageViewer);
        }
    }

    void decodeError(Tile tile, DecodeHandler.DecodeErrorException exception) {
        if (SLogType.LARGE.isEnabled()) {
            SLog.w(SLogType.LARGE, NAME, "decode failed. %s. tile=%s, tiles=%d",
                    exception.getCauseMessage(), tile.getInfo(), tileList.size());
        }

        tileList.remove(tile);

        tile.clean(bitmapPool);
        tilePool.put(tile);
    }

    void clean(String why) {
        for (Tile tile : tileList) {
            tile.refreshKey();
            tile.clean(bitmapPool);
            tilePool.put(tile);
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "clean tile and refresh key. %s. tile=%s", why, tile.getInfo());
            }
        }
        tileList.clear();
        visibleRect.setEmpty();
        drawRect.setEmpty();
        drawSrcRect.setEmpty();
        decodeRect.setEmpty();
        decodeSrcRect.setEmpty();
    }

    void recycle(@SuppressWarnings("UnusedParameters") String why) {
        clean(why);
        tilePool.clear();
        rectPool.clear();
    }
}