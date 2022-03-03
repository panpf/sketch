/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.zoom.newapi.block

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.internal.calculateInSampleSize
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.zoom.block.internal.ObjectPool
import com.github.panpf.sketch.zoom.internal.isCross
import com.github.panpf.sketch.zoom.newapi.block.NewBlock.Companion.blockListToString
import com.github.panpf.sketch.zoom.newapi.block.NewDecodeHandler.DecodeErrorException
import java.util.Collections
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 碎片管理器
 */
// TODO: 2016/12/17 优化碎片计算规则，尽量保证每块碎片的尺寸都是一样的，这样就能充分利用inBitmap功能减少内存分配提高流畅度
// todo 重构
class NewBlockManager(
    context: Context,
    private val blockDisplayer: Blocks,
) {

    companion object {
        private const val MODULE = "BlockManager"
    }

    private val visibleRect = Rect() // 可见区域，当前用户真正能看见的区域
    private val bitmapPool: BitmapPool = context.sketch.bitmapPool
    private val blockPool: ObjectPool<NewBlock> = ObjectPool({ NewBlock() }, 60)
    private val rectPool: ObjectPool<Rect> = ObjectPool({ Rect() }, 20)
    private val logger = context.sketch.logger

    var blockBaseNumber = 3 // 碎片基数，例如碎片基数是3时，就将绘制区域分割成一个(3+1)x(3+1)=16个方块
    var drawRect = Rect() // 绘制区域，可见区域加大一圈就是绘制区域，为的是提前将四周加载出来，用户缓慢滑动时可直接看到
    var decodeRect = Rect() // 解码区域，真正需要解码的区域，是以绘制区域为基础，滑动时哪边不够了就在扩展哪边，解码区域一定比绘制区域大
    var drawSrcRect = Rect()
    var decodeSrcRect = Rect()
    var blockList: MutableList<NewBlock> = LinkedList()

    fun update(
        newVisibleRect: Rect,
        drawableSize: Size,
        viewSize: Size,
        imageSize: Size,
        zooming: Boolean
    ) {
        if (zooming) {
            logger.d(MODULE) {
                "zooming. newVisibleRect=${newVisibleRect.toShortString()}, blocks=${blockList.size}"
            }
            return
        }

        // 过滤掉重复的刷新
        if (visibleRect == newVisibleRect) {
            logger.v(MODULE) {
                "visible rect no changed. update. newVisibleRect=%s, oldVisibleRect=%s"
                    .format(newVisibleRect.toShortString(), visibleRect.toShortString())
            }
            return
        }
        visibleRect.set(newVisibleRect)
        val imageWidth = imageSize.width
        val imageHeight = imageSize.height

        // 原始图和预览图对比的缩放比例
        val originWidthScale = imageWidth.toFloat() / drawableSize.width
        val originHeightScale = imageHeight.toFloat() / drawableSize.height

        // 计算绘制区域时，每边应该增加的量
        val drawWidthAdd = (newVisibleRect.width().toFloat() / blockBaseNumber / 2).toInt()
        val drawHeightAdd = (newVisibleRect.height()
            .toFloat() / blockBaseNumber / 2).toInt()

        // 将显示区域加大一圈，计算出绘制区域，宽高各增加一个平均值
        // 为的是提前将四周加载出来，用户缓慢滑动的时候可以提前看到四周的图像
        val newDrawRect = rectPool.get()
        newDrawRect.left = 0.coerceAtLeast(newVisibleRect.left - drawWidthAdd)
        newDrawRect.top = 0.coerceAtLeast(newVisibleRect.top - drawHeightAdd)
        newDrawRect.right = drawableSize.width.coerceAtMost(newVisibleRect.right + drawWidthAdd)
        newDrawRect.bottom = drawableSize.height.coerceAtMost(newVisibleRect.bottom + drawHeightAdd)
        if (newDrawRect.isEmpty) {
            logger.e(MODULE, "newDrawRect is empty. ${newDrawRect.toShortString()}")
            return
        }

        // 计算碎片的尺寸
        val finalBlocks = blockBaseNumber + 1
        val blockWidth = newDrawRect.width() / finalBlocks
        val blockHeight = newDrawRect.height() / finalBlocks
        if (blockWidth <= 0 || blockHeight <= 0) {
            logger.e(MODULE, "blockWidth or blockHeight exception. ${blockWidth}x${blockHeight}")
            return
        }

        // 根据碎片尺寸修剪drawRect，使其正好能整除碎片
        if (newDrawRect.right < drawableSize.width) {
            newDrawRect.right = newDrawRect.left + finalBlocks * blockWidth
        } else if (newDrawRect.left > 0) {
            newDrawRect.left = newDrawRect.right - finalBlocks * blockWidth
        }
        if (newDrawRect.bottom < drawableSize.height) {
            newDrawRect.bottom = newDrawRect.top + finalBlocks * blockHeight
        } else if (newDrawRect.top > 0) {
            newDrawRect.top = newDrawRect.bottom - finalBlocks * blockHeight
        }
        val newDrawSrcRect = rectPool.get()
        calculateSrcRect(
            newDrawSrcRect,
            newDrawRect,
            imageWidth,
            imageHeight,
            originWidthScale,
            originHeightScale
        )
        val inSampleSize = calculateInSampleSizeDelegate(
            newDrawSrcRect.width(),
            newDrawSrcRect.height(),
            viewSize.width,
            viewSize.height
        )
        logger.v(MODULE) {
            "update start. newVisibleRect=%s, newDrawRect=%s, oldDecodeRect=%s, inSampleSize=%d, scale=%s, lastScale=%s, blocks=%d"
                .format(
                    newVisibleRect.toShortString(),
                    newDrawRect.toShortString(),
                    decodeRect.toShortString(),
                    inSampleSize,
                    blockDisplayer.zoomScale,
                    blockDisplayer.lastZoomScale,
                    blockList.size
                )
        }

        // 根据上一次绘制区域的和新绘制区域的差异计算出最终的绘制区域
        val newDecodeRect = rectPool.get()
        calculateBlocksDecodeRect(
            newDecodeRect, newDrawRect, drawWidthAdd, drawHeightAdd,
            blockWidth, blockHeight, drawableSize.width, drawableSize.height
        )
        val newDecodeSrcRect = rectPool.get()
        calculateSrcRect(
            newDecodeSrcRect, newDecodeRect, imageWidth, imageHeight,
            originWidthScale, originHeightScale
        )
        if (!newDecodeRect.isEmpty) {
            // 如果最终绘制区域跟上一次没有变化就不继续了
            if (newDecodeRect != decodeRect) {

                // 回收那些已经超出绘制区域的碎片
                recycleBlocks(blockList, newDecodeRect)

                // 找出所有的空白区域，然后一个一个加载
                val emptyRectList = findEmptyRect(newDecodeRect, blockList)
                if (emptyRectList != null && emptyRectList.isNotEmpty()) {
                    loadBlocks(
                        emptyRectList, blockWidth, blockHeight, imageWidth, imageHeight,
                        originWidthScale, originHeightScale, inSampleSize, newDecodeRect
                    )
                } else {
                    logger.v(MODULE) { "not found empty rect" }
                }
                val onBlockChangedListener = blockDisplayer.onBlockChangedListener
                onBlockChangedListener?.onBlockChanged(blockDisplayer)
                logger.v(MODULE) {
                    "update finished, newDecodeRect=%s, blocks=%d"
                        .format(newDecodeRect.toShortString(), blockList.size)
                }
            } else {
                logger.v(MODULE) { "update finished draw rect no change" }
            }
        } else {
            logger.v(MODULE) {
                "update finished. final draw rect is empty. newDecodeRect=${newDecodeRect.toShortString()}"
            }
        }
        drawRect.set(newDrawRect)
        drawSrcRect.set(newDrawSrcRect)
        decodeRect.set(newDecodeRect)
        decodeSrcRect.set(newDecodeSrcRect)
        newDrawRect.setEmpty()
        newDrawSrcRect.setEmpty()
        newDecodeRect.setEmpty()
        newDecodeSrcRect.setEmpty()
        rectPool.put(newDrawRect)
        rectPool.put(newDrawSrcRect)
        rectPool.put(newDecodeRect)
        rectPool.put(newDecodeSrcRect)
    }

    /**
     * 计算绘制区域在完整图片中对应的区域，重点是各用各的缩放比例（这很重要），因为宽或高的比例可能不一样
     */
    private fun calculateSrcRect(
        srcRect: Rect, drawRect: Rect, imageWidth: Int, imageHeight: Int,
        originWidthScale: Float, originHeightScale: Float
    ) {
        srcRect.left = 0.coerceAtLeast((drawRect.left * originWidthScale).roundToInt())
        srcRect.top = 0.coerceAtLeast((drawRect.top * originHeightScale).roundToInt())
        srcRect.right = imageWidth.coerceAtMost((drawRect.right * originWidthScale).roundToInt())
        srcRect.bottom =
            imageHeight.coerceAtMost((drawRect.bottom * originHeightScale).roundToInt())
    }

    /**
     * 计算解码时的缩放比例
     */
    private fun calculateInSampleSizeDelegate(
        srcWidth: Int, srcHeight: Int, viewWidth: Int, viewHeight: Int
    ): Int {
        // 由于绘制区域比显示区域大了一圈，因此targetSize也得大一圈
        val targetSizeScale = blockBaseNumber.toFloat() / 10 + 1
        val targetWidth = (viewWidth * targetSizeScale).roundToInt()
        val targetHeight = (viewHeight * targetSizeScale).roundToInt()
        return calculateInSampleSize(srcWidth, srcHeight, targetWidth, targetHeight)
    }

    /**
     * 在上一个绘制区域的基础上计算出根据新的绘制区域，计算出最终的绘制区域
     */
    private fun calculateBlocksDecodeRect(
        newDecodeRect: Rect, newDrawRect: Rect,
        drawWidthAdd: Int, drawHeightAdd: Int,
        drawBlockWidth: Int, drawBlockHeight: Int,
        maxDrawWidth: Int, maxDrawHeight: Int
    ) {
        // 缩放比例已改变或者这是第一次就直接用新的绘制区域
        if (blockDisplayer.zoomScale != blockDisplayer.lastZoomScale || decodeRect.isEmpty) {
            newDecodeRect.set(newDrawRect)
            return
        }

        // 以上一次的绘制区域为基础
        newDecodeRect.set(decodeRect)
        val leftAndRightEdge = (drawWidthAdd * 0.8f).roundToInt()
        val topAndBottomEdge = (drawHeightAdd * 0.8f).roundToInt()
        val leftSpace = abs(newDrawRect.left - newDecodeRect.left)
        val topSpace = abs(newDrawRect.top - newDecodeRect.top)
        val rightSpace = abs(newDrawRect.right - newDecodeRect.right)
        val bottomSpace = abs(newDrawRect.bottom - newDecodeRect.bottom)

        // 左边需要加一列
        if (newDrawRect.left < newDecodeRect.left) {
            if (newDrawRect.left == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.left = 0
                logger.v(MODULE) {
                    "decode rect left to 0, newDecodeRect=${newDecodeRect.toShortString()}"
                }
            } else if (leftSpace > leftAndRightEdge || newDecodeRect.left - drawBlockWidth <= 0) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (newDecodeRect.left > newDrawRect.left) {
                    newDecodeRect.left = 0.coerceAtLeast(newDecodeRect.left - drawBlockWidth)
                    logger.v(MODULE) {
                        "decode rect left expand %d, newDecodeRect=%s"
                            .format(drawBlockWidth, newDecodeRect.toShortString())
                    }
                }
            }
        }

        // 顶部需要加一行
        if (newDrawRect.top < newDecodeRect.top) {
            if (newDrawRect.top == 0) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.top = 0
                logger.v(MODULE) {
                    "decode rect top to 0, newDecodeRect=${newDecodeRect.toShortString()}"
                }
            } else if (topSpace > topAndBottomEdge || newDecodeRect.top - drawBlockHeight <= 0) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (newDecodeRect.top > newDrawRect.top) {
                    newDecodeRect.top = 0.coerceAtLeast(newDecodeRect.top - drawBlockHeight)
                    logger.v(MODULE) {
                        "decode rect top expand %d, newDecodeRect=%s"
                            .format(drawBlockHeight, newDecodeRect.toShortString())
                    }
                }
            }
        }


        // 右边需要加一列
        if (newDrawRect.right > newDecodeRect.right) {
            if (newDrawRect.right == maxDrawWidth) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.right = maxDrawWidth
                logger.v(MODULE) {
                    "decode rect right to %d, newDecodeRect=%s"
                        .format(maxDrawWidth, newDecodeRect.toShortString())
                }
            } else if (rightSpace > leftAndRightEdge || newDecodeRect.right + drawBlockWidth >= maxDrawWidth) {
                // 如果间距比较大或者再加一个碎片的宽度就到边了就扩展
                // 由于间距可能会大于一个碎片的宽度，因此要循环不停的加
                while (newDecodeRect.right < newDrawRect.right) {
                    newDecodeRect.right =
                        maxDrawWidth.coerceAtMost(newDecodeRect.right + drawBlockWidth)
                    logger.v(
                        MODULE
                    ) {
                        "decode rect right expand %d, newDecodeRect=%s".format(
                            drawBlockWidth, newDecodeRect.toShortString()
                        )
                    }
                }
            }
        }

        // 底部需要加一行
        if (newDrawRect.bottom > newDecodeRect.bottom) {
            if (newDrawRect.bottom > maxDrawHeight) {
                // 如果已经到边了，管它还差多少，直接顶到边
                newDecodeRect.bottom = maxDrawHeight
                logger.v(
                    MODULE
                ) {
                    "decode rect bottom to %d, newDecodeRect=%s".format(
                        maxDrawHeight, newDecodeRect.toShortString()
                    )
                }
            } else if (bottomSpace > topAndBottomEdge || newDecodeRect.bottom + drawBlockHeight >= maxDrawHeight) {
                // 如果间距比较大或者再加一个碎片的高度就到边了就扩展
                // 由于间距可能会大于一个碎片的高度，因此要循环不停的加
                while (newDecodeRect.bottom < newDrawRect.bottom) {
                    newDecodeRect.bottom =
                        maxDrawHeight.coerceAtMost(newDecodeRect.bottom + drawBlockHeight)
                    logger.v(
                        MODULE
                    ) {
                        "decode rect bottom expand %d, newDecodeRect=%s".format(
                            drawBlockHeight, newDecodeRect.toShortString()
                        )
                    }
                }
            }
        }

        // 前面把四周给加大了一圈，这里要把多余的剪掉
        while (newDecodeRect.left + drawBlockWidth < newDrawRect.left || newDecodeRect.top + drawBlockHeight < newDrawRect.top || newDecodeRect.right - drawBlockWidth > newDrawRect.right || newDecodeRect.bottom - drawBlockHeight > newDrawRect.bottom) {
            if (newDecodeRect.left + drawBlockWidth < newDrawRect.left) {
                newDecodeRect.left += drawBlockWidth
                logger.v(
                    MODULE
                ) {
                    "decode rect left reduced %d, newDecodeRect=%s".format(
                        drawBlockWidth, newDecodeRect.toShortString()
                    )
                }
            }
            if (newDecodeRect.top + drawBlockHeight < newDrawRect.top) {
                newDecodeRect.top += drawBlockHeight
                logger.v(
                    MODULE
                ) {
                    "decode rect top reduced %d, newDecodeRect=%s".format(
                        drawBlockHeight, newDecodeRect.toShortString()
                    )
                }
            }
            if (newDecodeRect.right - drawBlockWidth > newDrawRect.right) {
                newDecodeRect.right -= drawBlockWidth
                logger.v(
                    MODULE
                ) {
                    "decode rect right reduced %d, newDecodeRect=%s".format(
                        drawBlockWidth, newDecodeRect.toShortString()
                    )
                }
            }
            if (newDecodeRect.bottom - drawBlockHeight > newDrawRect.bottom) {
                newDecodeRect.bottom -= drawBlockHeight
                logger.v(
                    MODULE
                ) {
                    "decode rect bottom reduced %d, newDecodeRect=%s".format(
                        drawBlockHeight, newDecodeRect.toShortString()
                    )
                }
            }
        }
    }

    /**
     * 去重
     */
    private fun canLoad(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        for (drawBlock in blockList) {
            if (drawBlock.drawRect.left == left && drawBlock.drawRect.top == top && drawBlock.drawRect.right == right && drawBlock.drawRect.bottom == bottom) {
                return false
            }
        }
        return true
    }

    /**
     * 假如有一个矩形，并且已知这个矩形中的N个碎片，那么要找出所有的空白碎片（不可用的碎片会从已知列表中删除）
     *
     * @param rect      那个矩形
     * @param blockList 已知碎片
     * @return 所有空白的碎片
     */
    private fun findEmptyRect(rect: Rect, blockList: MutableList<NewBlock>?): List<Rect>? {
        if (rect.isEmpty) {
            return null
        }
        var emptyRectList: MutableList<Rect>? = null
        if (blockList == null || blockList.size == 0) {
            val fullRect = rectPool.get()
            fullRect.set(rect)
            emptyRectList = LinkedList()
            emptyRectList.add(fullRect)
            return emptyRectList
        }

        // 按离左上角的距离排序
        val blockComparator = Comparator<NewBlock> { o1, o2 -> // 同一行比较left，不同行比较top
            if (o1.drawRect.top <= o2.drawRect.top && o1.drawRect.bottom >= o2.drawRect.bottom
                || o1.drawRect.top >= o2.drawRect.top && o1.drawRect.bottom <= o2.drawRect.bottom
            ) {
                o1.drawRect.left - o2.drawRect.left
            } else {
                o1.drawRect.top - o2.drawRect.top
            }
        }
        try {
            Collections.sort(blockList, blockComparator)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()

            /*
             * Java7的排序算法在检测到A>B, B>C, 但是A<=C的时候就会抛出异常，这里的处理办法是暂时改用旧版的排序算法再次排序
             */
            logger.e(MODULE, "onBlockSortError. ${blockListToString(blockList)}")
//            with(appContext).configuration.callback.onError(BlockSortException(e, blockList, false))
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true")
            try {
                Collections.sort(blockList, blockComparator)
            } catch (e2: IllegalArgumentException) {
                e2.printStackTrace()
                logger.e(
                    MODULE,
                    "onBlockSortError. useLegacyMergeSort. ${blockListToString(blockList)!!}",
                )
//                with(appContext).configuration.callback.onError(
//                    BlockSortException(e, blockList, false)
//                )
            }
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "false")
        }
        val left = rect.left
        var top = rect.top
        var right = 0
        var bottom = -1
        var lastRect: NewBlock? = null
        var childRect: NewBlock
        val rectIterator = blockList.iterator()
        while (rectIterator.hasNext()) {
            childRect = rectIterator.next()
            val newLine = lastRect == null || childRect.drawRect.top >= bottom
            if (newLine) {
                // 首先要处理上一行的最后一个
                if (lastRect != null) {
                    if (lastRect.drawRect.right < rect.right) {
                        val rightEmptyRect = rectPool.get()
                        rightEmptyRect[lastRect.drawRect.right, top, rect.right] = bottom
                        if (emptyRectList == null) {
                            emptyRectList = LinkedList()
                        }
                        emptyRectList.add(rightEmptyRect)
                    }
                }

                // 然后要更新top和bottom
                top = if (bottom != -1) bottom else top
                bottom = childRect.drawRect.bottom

                // 左边有空隙
                if (childRect.drawRect.left > left) {
                    val leftEmptyRect = rectPool.get()
                    leftEmptyRect[left, childRect.drawRect.top, childRect.drawRect.left] =
                        childRect.drawRect.bottom
                    if (emptyRectList == null) {
                        emptyRectList = LinkedList()
                    }
                    emptyRectList.add(leftEmptyRect)
                }

                // 顶部有空隙
                if (childRect.drawRect.top > top) {
                    val topEmptyRect = rectPool.get()
                    topEmptyRect[left, top, childRect.drawRect.right] = childRect.drawRect.top
                    if (emptyRectList == null) {
                        emptyRectList = LinkedList()
                    }
                    emptyRectList.add(topEmptyRect)
                }
                right = childRect.drawRect.right
                lastRect = childRect
            } else {
                val available = childRect.drawRect.bottom == lastRect!!.drawRect.bottom
                if (available) {
                    // 左边有空隙
                    if (childRect.drawRect.left > right) {
                        val leftEmptyRect = rectPool.get()
                        leftEmptyRect[right, top, childRect.drawRect.left] = bottom
                        if (emptyRectList == null) {
                            emptyRectList = LinkedList()
                        }
                        emptyRectList.add(leftEmptyRect)
                    }

                    // 顶部有空隙
                    if (childRect.drawRect.top > top) {
                        val topEmptyRect = rectPool.get()
                        topEmptyRect[childRect.drawRect.left, top, childRect.drawRect.right] =
                            childRect.drawRect.top
                        if (emptyRectList == null) {
                            emptyRectList = LinkedList()
                        }
                        emptyRectList.add(topEmptyRect)
                    }
                    right = childRect.drawRect.right
                    lastRect = childRect
                } else {
                    rectIterator.remove()
                }
            }
        }

        // 最后的结尾处理
        if (right < rect.right) {
            val rightEmptyRect = rectPool.get()
            rightEmptyRect[right, top, rect.right] = bottom
            if (emptyRectList == null) {
                emptyRectList = LinkedList()
            }
            emptyRectList.add(rightEmptyRect)
        }
        if (bottom < rect.bottom) {
            val bottomEmptyRect = rectPool.get()
            bottomEmptyRect[rect.left, bottom, rect.right] = rect.bottom
            if (emptyRectList == null) {
                emptyRectList = LinkedList()
            }
            emptyRectList.add(bottomEmptyRect)
        }
        return emptyRectList
    }

    /**
     * 回收哪些已经超出绘制区域的碎片
     */
    private fun recycleBlocks(blockList: MutableList<NewBlock>, drawRect: Rect) {
        var block: NewBlock
        val blockIterator = blockList.iterator()
        while (blockIterator.hasNext()) {
            block = blockIterator.next()

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以回收这个碎片了
            if (blockDisplayer.zoomScale != block.scale || !block.drawRect.isCross(drawRect)) {
                if (!block.isEmpty) {
                    logger.v(MODULE) { "recycle block. block=${block.info}" }
                    blockIterator.remove()
                    block.clean(bitmapPool)
                    blockPool.put(block)
                } else {
                    logger.v(MODULE) { "recycle loading block and refresh key. block=${block.info}" }
                    block.refreshKey()
                    blockIterator.remove()
                }
            }
        }
    }

    private fun loadBlocks(
        emptyRectList: List<Rect>, blockWidth: Int, blockHeight: Int,
        imageWidth: Int, imageHeight: Int, originWidthScale: Float, originHeightScale: Float,
        inSampleSize: Int, newDecodeRect: Rect
    ) {
        for (emptyRect in emptyRectList) {
            logger.v(MODULE) { "load emptyRect=${emptyRect.toShortString()}" }
            var blockLeft = emptyRect.left
            var blockTop = emptyRect.top
            var blockRight = 0
            var blockBottom = 0
            while (
                blockRight.toFloat().roundToInt() < emptyRect.right
                || blockBottom.toFloat().roundToInt() < emptyRect.bottom
            ) {
                blockRight = (blockLeft + blockWidth).coerceAtMost(emptyRect.right)
                blockBottom = (blockTop + blockHeight).coerceAtMost(emptyRect.bottom)
                if (canLoad(blockLeft, blockTop, blockRight, blockBottom)) {
                    val loadBlock = blockPool.get()
                    loadBlock.drawRect[blockLeft, blockTop, blockRight] = blockBottom
                    loadBlock.inSampleSize = inSampleSize
                    loadBlock.scale = blockDisplayer.zoomScale
                    calculateSrcRect(
                        loadBlock.srcRect,
                        loadBlock.drawRect,
                        imageWidth,
                        imageHeight,
                        originWidthScale,
                        originHeightScale
                    )
                    blockList.add(loadBlock)
                    logger.v(
                        MODULE
                    ) {
                        "submit and refresh key. newDecodeRect=%s, block=%s".format(
                            newDecodeRect.toShortString(), loadBlock.info
                        )
                    }
                    loadBlock.refreshKey()
                    blockDisplayer.blockDecoder.decodeBlock(loadBlock)
                } else {
                    logger.v(
                        MODULE
                    ) {
                        "repeated block. blockDrawRect=%d, %d, %d, %d".format(
                            blockLeft.toFloat().roundToInt(),
                            blockTop.toFloat().roundToInt(),
                            blockRight.toFloat().roundToInt(),
                            blockBottom.toFloat().roundToInt()
                        )
                    }
                }
                if (blockRight.toFloat().roundToInt() >= emptyRect.right) {
                    blockLeft = emptyRect.left
                    blockTop = blockBottom
                } else {
                    blockLeft = blockRight
                }
            }
            emptyRect.setEmpty()
            rectPool.put(emptyRect)
        }
    }

    fun decodeCompleted(block: NewBlock, bitmap: Bitmap, useTime: Int) {
        logger.v(
            MODULE
        ) {
            val bitmapConfig = if (bitmap.config != null) bitmap.config.name else null
            "decode completed. useTime=%dms, block=%s, bitmap=%dx%d(%s), blocks=%d".format(
                useTime, block.info, bitmap.width, bitmap.height, bitmapConfig!!, blockList.size
            )
        }
        block.bitmap = bitmap
        block.bitmapDrawSrcRect[0, 0, bitmap.width] = bitmap.height
        block.decoder = null
        blockDisplayer.invalidateView()
        val onBlockChangedListener = blockDisplayer.onBlockChangedListener
        onBlockChangedListener?.onBlockChanged(blockDisplayer)
    }

    fun decodeError(block: NewBlock, exception: DecodeErrorException) {
        logger.w(
            MODULE, "decode failed. %s. block=%s, blocks=%d".format(
                exception.causeMessage, block.info, blockList.size
            )
        )
        blockList.remove(block)
        block.clean(bitmapPool)
        blockPool.put(block)
    }

    fun clean(why: String?) {
        for (block in blockList) {
            block.refreshKey()
            block.clean(bitmapPool)
            blockPool.put(block)
            logger.v(MODULE) {
                "clean block and refresh key. %s. block=%s".format(why!!, block.info)
            }
        }
        blockList.clear()
        visibleRect.setEmpty()
        drawRect.setEmpty()
        drawSrcRect.setEmpty()
        decodeRect.setEmpty()
        decodeSrcRect.setEmpty()
    }

    val allocationByteCount: Long
        get() {
            if (blockList.size <= 0) {
                return 0
            }
            var bytes: Long = 0
            for (block in blockList) {
                if (!block.isEmpty) {
                    bytes += block.bitmap?.byteCountCompat?.toLong() ?: 0
                }
            }
            return bytes
        }

    fun recycle(why: String?) {
        clean(why)
        blockPool.clear()
        rectPool.clear()
    }
}