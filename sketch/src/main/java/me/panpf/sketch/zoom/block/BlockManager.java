/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom.block;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.ErrorTracker;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageSizeCalculator;
import me.panpf.sketch.util.ObjectPool;
import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.zoom.BlockDisplayer;
import me.panpf.sketch.zoom.Size;

/**
 * 碎片管理器
 */
// TODO: 2016/12/17 优化碎片计算规则，尽量保证每块碎片的尺寸都是一样的，这样就能充分利用inBitmap功能减少内存分配提高流畅度
public class BlockManager {
    private static final String NAME = "BlockManager";

    public int blockBaseNumber = 3;  // 碎片基数，例如碎片基数是3时，就将绘制区域分割成一个(3+1)x(3+1)=16个方块
    public Rect drawRect = new Rect(); // 绘制区域，可见区域加大一圈就是绘制区域，为的是提前将四周加载出来，用户缓慢滑动时可直接看到
    public Rect decodeRect = new Rect();   // 解码区域，真正需要解码的区域，是以绘制区域为基础，滑动时哪边不够了就在扩展哪边，解码区域一定比绘制区域大
    public Rect drawSrcRect = new Rect();
    public Rect decodeSrcRect = new Rect();
    public List<Block> blockList = new LinkedList<Block>();
    public Rect visibleRect = new Rect();  // 可见区域，当前用户真正能看见的区域

    private Context context;
    private BitmapPool bitmapPool;
    private BlockDisplayer blockDisplayer;
    private ObjectPool<Block> blockPool = new ObjectPool<>(new ObjectPool.ObjectFactory<Block>() {
        @Override
        public Block newObject() {
            return new Block();
        }
    }, 60);
    private ObjectPool<Rect> rectPool = new ObjectPool<>(new ObjectPool.ObjectFactory<Rect>() {
        @Override
        public Rect newObject() {
            return new Rect();
        }
    }, 20);

    public BlockManager(Context context, BlockDisplayer blockDisplayer) {
        context = context.getApplicationContext();
        this.context = context;
        this.bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
        this.blockDisplayer = blockDisplayer;
    }

    public void update(Rect newVisibleRect, Size drawableSize, Size viewSize, Point imageSize, boolean zooming) {
        if (zooming) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM)) {
                SLog.d(NAME, "zooming. newVisibleRect=%s, blocks=%d",
                        newVisibleRect.toShortString(), blockList.size());
            }
            return;
        }

        // 过滤掉重复的刷新
        if (visibleRect.equals(newVisibleRect)) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "visible rect no changed. update. newVisibleRect=%s, oldVisibleRect=%s",
                        newVisibleRect.toShortString(), visibleRect.toShortString());
            }
            return;
        }
        visibleRect.set(newVisibleRect);

        final int imageWidth = imageSize.x;
        final int imageHeight = imageSize.y;

        // 原始图和预览图对比的缩放比例
        final float originWidthScale = (float) imageWidth / drawableSize.getWidth();
        final float originHeightScale = (float) imageHeight / drawableSize.getHeight();

        // 计算绘制区域时，每边应该增加的量
        final int drawWidthAdd = (int) ((float) newVisibleRect.width() / blockBaseNumber / 2);
        final int drawHeightAdd = (int) ((float) newVisibleRect.height() / blockBaseNumber / 2);

        // 将显示区域加大一圈，计算出绘制区域，宽高各增加一个平均值
        // 为的是提前将四周加载出来，用户缓慢滑动的时候可以提前看到四周的图像
        Rect newDrawRect = rectPool.get();
        newDrawRect.left = Math.max(0, newVisibleRect.left - drawWidthAdd);
        newDrawRect.top = Math.max(0, newVisibleRect.top - drawHeightAdd);
        newDrawRect.right = Math.min(drawableSize.getWidth(), newVisibleRect.right + drawWidthAdd);
        newDrawRect.bottom = Math.min(drawableSize.getHeight(), newVisibleRect.bottom + drawHeightAdd);

        if (newDrawRect.isEmpty()) {
            SLog.e(NAME, "newDrawRect is empty. %s", newDrawRect.toShortString());
            return;
        }

        // 计算碎片的尺寸
        final int finalBlocks = blockBaseNumber + 1;
        final int blockWidth = newDrawRect.width() / finalBlocks;
        final int blockHeight = newDrawRect.height() / finalBlocks;

        if (blockWidth <= 0 || blockHeight <= 0) {
            SLog.e(NAME, "blockWidth or blockHeight exception. %dx%d", blockWidth, blockHeight);
            return;
        }

        // 根据碎片尺寸修剪drawRect，使其正好能整除碎片
        if (newDrawRect.right < drawableSize.getWidth()) {
            newDrawRect.right = newDrawRect.left + (finalBlocks * blockWidth);
        } else if (newDrawRect.left > 0) {
            newDrawRect.left = newDrawRect.right - (finalBlocks * blockWidth);
        }
        if (newDrawRect.bottom < drawableSize.getHeight()) {
            newDrawRect.bottom = newDrawRect.top + (finalBlocks * blockHeight);
        } else if (newDrawRect.top > 0) {
            newDrawRect.top = newDrawRect.bottom - (finalBlocks * blockHeight);
        }

        Rect newDrawSrcRect = rectPool.get();
        calculateSrcRect(newDrawSrcRect, newDrawRect, imageWidth, imageHeight, originWidthScale, originHeightScale);
        int inSampleSize = calculateInSampleSize(newDrawSrcRect.width(), newDrawSrcRect.height(), viewSize.getWidth(), viewSize.getHeight());

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            SLog.d(NAME, "update start. newVisibleRect=%s, newDrawRect=%s, oldDecodeRect=%s, inSampleSize=%d, scale=%s, lastScale=%s, blocks=%d",
                    newVisibleRect.toShortString(), newDrawRect.toShortString(), decodeRect.toShortString(),
                    inSampleSize, blockDisplayer.getZoomScale(), blockDisplayer.getLastZoomScale(), blockList.size());
        }

        // 根据上一次绘制区域的和新绘制区域的差异计算出最终的绘制区域
        Rect newDecodeRect = rectPool.get();
        calculateBlocksDecodeRect(newDecodeRect, newDrawRect, drawWidthAdd, drawHeightAdd,
                blockWidth, blockHeight, drawableSize.getWidth(), drawableSize.getHeight());

        Rect newDecodeSrcRect = rectPool.get();
        calculateSrcRect(newDecodeSrcRect, newDecodeRect, imageWidth, imageHeight,
                originWidthScale, originHeightScale);

        if (!newDecodeRect.isEmpty()) {
            // 如果最终绘制区域跟上一次没有变化就不继续了
            if (!newDecodeRect.equals(decodeRect)) {

                // 回收那些已经超出绘制区域的碎片
                recycleBlocks(blockList, newDecodeRect);

                // 找出所有的空白区域，然后一个一个加载
                List<Rect> emptyRectList = findEmptyRect(newDecodeRect, blockList);
                if (emptyRectList != null && emptyRectList.size() > 0) {
                    loadBlocks(emptyRectList, blockWidth, blockHeight, imageWidth, imageHeight,
                            originWidthScale, originHeightScale, inSampleSize, newDecodeRect);
                } else {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "not found empty rect");
                    }
                }

                BlockDisplayer.OnBlockChangedListener onBlockChangedListener = blockDisplayer.getOnBlockChangedListener();
                if (onBlockChangedListener != null) {
                    onBlockChangedListener.onBlockChanged(blockDisplayer);
                }

                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "update finished, newDecodeRect=%s, blocks=%d",
                            newDecodeRect.toShortString(), blockList.size());
                }
            } else {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "update finished draw rect no change");
                }
            }
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "update finished. final draw rect is empty. newDecodeRect=%s",
                        newDecodeRect.toShortString());
            }
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
        float targetSizeScale = ((float) blockBaseNumber / 10) + 1;
        int targetWidth = Math.round(viewWidth * targetSizeScale);
        int targetHeight = Math.round(viewHeight * targetSizeScale);

        ImageSizeCalculator sizeCalculator = Sketch.with(context).getConfiguration().getSizeCalculator();
        return sizeCalculator.calculateInSampleSize(srcWidth, srcHeight, targetWidth, targetHeight, false);
    }

    /**
     * 在上一个绘制区域的基础上计算出根据新的绘制区域，计算出最终的绘制区域
     */
    private void calculateBlocksDecodeRect(Rect newDecodeRect, Rect newDrawRect,
                                           int drawWidthAdd, int drawHeightAdd,
                                           int drawBlockWidth, int drawBlockHeight,
                                           int maxDrawWidth, int maxDrawHeight) {
        // 缩放比例已改变或者这是第一次就直接用新的绘制区域
        if (blockDisplayer.getZoomScale() != blockDisplayer.getLastZoomScale() || decodeRect.isEmpty()) {
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
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect left to 0, newDecodeRect=%s", newDecodeRect.toShortString());
                }
            } else if (leftSpace > leftAndRightEdge || newDecodeRect.left - drawBlockWidth <= 0) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (newDecodeRect.left > newDrawRect.left) {
                    newDecodeRect.left = Math.max(0, newDecodeRect.left - drawBlockWidth);
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "decode rect left expand %d, newDecodeRect=%s",
                                drawBlockWidth, newDecodeRect.toShortString());
                    }
                }
            }
        }

        // 顶部需要加一行
        if (newDrawRect.top < newDecodeRect.top) {
            if (newDrawRect.top == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.top = 0;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect top to 0, newDecodeRect=%s", newDecodeRect.toShortString());
                }
            } else if (topSpace > topAndBottomEdge || newDecodeRect.top - drawBlockHeight <= 0) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (newDecodeRect.top > newDrawRect.top) {
                    newDecodeRect.top = Math.max(0, newDecodeRect.top - drawBlockHeight);
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "decode rect top expand %d, newDecodeRect=%s",
                                drawBlockHeight, newDecodeRect.toShortString());
                    }
                }
            }
        }


        // 右边需要加一列
        if (newDrawRect.right > newDecodeRect.right) {
            if (newDrawRect.right == maxDrawWidth) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.right = maxDrawWidth;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect right to %d, newDecodeRect=%s",
                            maxDrawWidth, newDecodeRect.toShortString());
                }
            } else if (rightSpace > leftAndRightEdge || newDecodeRect.right + drawBlockWidth >= maxDrawWidth) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (newDecodeRect.right < newDrawRect.right) {
                    newDecodeRect.right = Math.min(maxDrawWidth, newDecodeRect.right + drawBlockWidth);
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "decode rect right expand %d, newDecodeRect=%s",
                                drawBlockWidth, newDecodeRect.toShortString());
                    }
                }
            }
        }

        // 底部需要加一行
        if (newDrawRect.bottom > newDecodeRect.bottom) {
            if (newDrawRect.bottom > maxDrawHeight) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.bottom = maxDrawHeight;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect bottom to %d, newDecodeRect=%s",
                            maxDrawHeight, newDecodeRect.toShortString());
                }
            } else if (bottomSpace > topAndBottomEdge || newDecodeRect.bottom + drawBlockHeight >= maxDrawHeight) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (newDecodeRect.bottom < newDrawRect.bottom) {
                    newDecodeRect.bottom = Math.min(maxDrawHeight, newDecodeRect.bottom + drawBlockHeight);
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "decode rect bottom expand %d, newDecodeRect=%s",
                                drawBlockHeight, newDecodeRect.toShortString());
                    }
                }
            }
        }

        // 前面把四周给加大了一圈，这里要把多余的剪掉
        while (newDecodeRect.left + drawBlockWidth < newDrawRect.left ||
                newDecodeRect.top + drawBlockHeight < newDrawRect.top ||
                newDecodeRect.right - drawBlockWidth > newDrawRect.right ||
                newDecodeRect.bottom - drawBlockHeight > newDrawRect.bottom) {
            if (newDecodeRect.left + drawBlockWidth < newDrawRect.left) {
                newDecodeRect.left += drawBlockWidth;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect left reduced %d, newDecodeRect=%s",
                            drawBlockWidth, newDecodeRect.toShortString());
                }
            }
            if (newDecodeRect.top + drawBlockHeight < newDrawRect.top) {
                newDecodeRect.top += drawBlockHeight;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect top reduced %d, newDecodeRect=%s",
                            drawBlockHeight, newDecodeRect.toShortString());
                }
            }
            if (newDecodeRect.right - drawBlockWidth > newDrawRect.right) {
                newDecodeRect.right -= drawBlockWidth;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect right reduced %d, newDecodeRect=%s",
                            drawBlockWidth, newDecodeRect.toShortString());
                }
            }
            if (newDecodeRect.bottom - drawBlockHeight > newDrawRect.bottom) {
                newDecodeRect.bottom -= drawBlockHeight;
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "decode rect bottom reduced %d, newDecodeRect=%s",
                            drawBlockHeight, newDecodeRect.toShortString());
                }
            }
        }
    }

    /**
     * 去重
     */
    private boolean canLoad(int left, int top, int right, int bottom) {
        for (Block drawBlock : blockList) {
            if (drawBlock.drawRect.left == left &&
                    drawBlock.drawRect.top == top &&
                    drawBlock.drawRect.right == right &&
                    drawBlock.drawRect.bottom == bottom) {
                return false;
            }
        }

        return true;
    }

    /**
     * 假如有一个矩形，并且已知这个矩形中的N个碎片，那么要找出所有的空白碎片（不可用的碎片会从已知列表中删除）
     *
     * @param rect      那个矩形
     * @param blockList 已知碎片
     * @return 所有空白的碎片
     */
    private List<Rect> findEmptyRect(Rect rect, List<Block> blockList) {
        if (rect.isEmpty()) {
            return null;
        }

        List<Rect> emptyRectList = null;
        if (blockList == null || blockList.size() == 0) {
            Rect fullRect = rectPool.get();
            fullRect.set(rect);

            emptyRectList = new LinkedList<>();
            emptyRectList.add(fullRect);
            return emptyRectList;
        }

        // 按离左上角的距离排序
        Comparator<Block> blockComparator = new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
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
            Collections.sort(blockList, blockComparator);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            /**
             * Java7的排序算法在检测到A>B, B>C, 但是A<=C的时候就会抛出异常，这里的处理办法是暂时改用旧版的排序算法再次排序
             */

            Configuration configuration = Sketch.with(context).getConfiguration();
            ErrorTracker errorTracker = configuration.getErrorTracker();
            errorTracker.onBlockSortError(e, blockList, false);

            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            try {
                Collections.sort(blockList, blockComparator);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();

                errorTracker.onBlockSortError(e, blockList, true);
            }
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "false");
        }

        int left = rect.left, top = rect.top, right = 0, bottom = -1;
        Block lastRect = null;
        Block childRect;
        Iterator<Block> rectIterator = blockList.iterator();
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
    private void recycleBlocks(List<Block> blockList, Rect drawRect) {
        Block block;
        Iterator<Block> blockIterator = blockList.iterator();
        while (blockIterator.hasNext()) {
            block = blockIterator.next();

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以回收这个碎片了
            if (blockDisplayer.getZoomScale() != block.scale || !SketchUtils.isCross(block.drawRect, drawRect)) {
                if (!block.isEmpty()) {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "recycle block. block=%s", block.getInfo());
                    }
                    blockIterator.remove();
                    block.clean(bitmapPool);
                    blockPool.put(block);
                } else {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "recycle loading block and refresh key. block=%s", block.getInfo());
                    }
                    block.refreshKey();
                    blockIterator.remove();
                }
            }
        }
    }

    private void loadBlocks(List<Rect> emptyRectList, int blockWidth, int blockHeight,
                            int imageWidth, int imageHeight, float originWidthScale, float originHeightScale,
                            int inSampleSize, Rect newDecodeRect) {
        for (Rect emptyRect : emptyRectList) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "load emptyRect=%s", emptyRect.toShortString());
            }

            int blockLeft = emptyRect.left, blockTop = emptyRect.top, blockRight = 0, blockBottom = 0;
            while (Math.round(blockRight) < emptyRect.right || Math.round(blockBottom) < emptyRect.bottom) {
                blockRight = Math.min(blockLeft + blockWidth, emptyRect.right);
                blockBottom = Math.min(blockTop + blockHeight, emptyRect.bottom);

                if (canLoad(blockLeft, blockTop, blockRight, blockBottom)) {
                    Block loadBlock = blockPool.get();

                    loadBlock.drawRect.set(blockLeft, blockTop, blockRight, blockBottom);
                    loadBlock.inSampleSize = inSampleSize;
                    loadBlock.scale = blockDisplayer.getZoomScale();
                    calculateSrcRect(loadBlock.srcRect, loadBlock.drawRect, imageWidth, imageHeight, originWidthScale, originHeightScale);

                    blockList.add(loadBlock);
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "submit and refresh key. newDecodeRect=%s, block=%s",
                                newDecodeRect.toShortString(), loadBlock.getInfo());
                    }

                    loadBlock.refreshKey();
                    blockDisplayer.getBlockDecoder().decodeBlock(loadBlock);
                } else {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                        SLog.d(NAME, "repeated block. blockDrawRect=%d, %d, %d, %d",
                                Math.round(blockLeft), Math.round(blockTop), Math.round(blockRight), Math.round(blockBottom));
                    }
                }

                if (Math.round(blockRight) >= emptyRect.right) {
                    blockLeft = emptyRect.left;
                    blockTop = blockBottom;
                } else {
                    blockLeft = blockRight;
                }
            }

            emptyRect.setEmpty();
            rectPool.put(emptyRect);
        }
    }

    public void decodeCompleted(Block block, Bitmap bitmap, int useTime) {
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
            String bitmapConfig = bitmap.getConfig() != null ? bitmap.getConfig().name() : null;
            SLog.d(NAME, "decode completed. useTime=%dms, block=%s, bitmap=%dx%d(%s), blocks=%d",
                    useTime, block.getInfo(), bitmap.getWidth(), bitmap.getHeight(), bitmapConfig, blockList.size());
        }

        block.bitmap = bitmap;
        block.bitmapDrawSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        block.decoder = null;

        blockDisplayer.invalidateView();

        BlockDisplayer.OnBlockChangedListener onBlockChangedListener = blockDisplayer.getOnBlockChangedListener();
        if (onBlockChangedListener != null) {
            onBlockChangedListener.onBlockChanged(blockDisplayer);
        }
    }

    public void decodeError(Block block, DecodeHandler.DecodeErrorException exception) {
        SLog.w(NAME, "decode failed. %s. block=%s, blocks=%d",
                exception.getCauseMessage(), block.getInfo(), blockList.size());

        blockList.remove(block);

        block.clean(bitmapPool);
        blockPool.put(block);
    }

    public void clean(String why) {
        for (Block block : blockList) {
            block.refreshKey();
            block.clean(bitmapPool);
            blockPool.put(block);
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "clean block and refresh key. %s. block=%s", why, block.getInfo());
            }
        }
        blockList.clear();
        visibleRect.setEmpty();
        drawRect.setEmpty();
        drawSrcRect.setEmpty();
        decodeRect.setEmpty();
        decodeSrcRect.setEmpty();
    }

    public long getAllocationByteCount() {
        if (blockList == null || blockList.size() <= 0) {
            return 0;
        }

        long bytes = 0;
        for (Block block : blockList) {
            if (!block.isEmpty()) {
                bytes += SketchUtils.getByteCount(block.bitmap);
            }
        }
        return bytes;
    }

    public void recycle(@SuppressWarnings("UnusedParameters") String why) {
        clean(why);
        blockPool.clear();
        rectPool.clear();
    }
}