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
package com.github.panpf.sketch.zoom.tile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.zoom.internal.getScale
import kotlin.math.roundToInt

class TileManager constructor(
    context: Context,
    private val imageUri: String,
    viewSize: Size,
    private val decoder: TileDecoder,
) {

    companion object {
        private const val MODULE = "TileManager"
    }

    private val logger = context.sketch.logger

    //    private var lastZoomScale = 0f
    private val drawBlockRectPaint: Paint by lazy {
        Paint().apply { color = Color.parseColor("#880000FF") }
    }
    private val drawLoadingBlockRectPaint: Paint by lazy {
        Paint().apply { color = Color.parseColor("#88FF0000") }
    }
    private val tileMap: Map<Int, List<Tile>> = initializeTileMap(decoder.imageSize, viewSize)
    private val bitmapPool: BitmapPool = context.sketch.bitmapPool
    private val memoryCache: MemoryCache = context.sketch.memoryCache
    private var currentTileList: List<Tile>? = null
    private var currentSampleSize: Int? = null

    var viewSize: Size = viewSize
        internal set(value) {
            if (field != value) {
                field = value
                // todo
//                reset()
            }
        }

    init {

    }

    fun refreshTiles(
        visibleRect: Rect,
        drawableSize: Size,
        drawMatrix: Matrix,
    ) {
        val newZoomScale = drawMatrix.getScale().format(2)
        val newSampleSize = findSampleSize(
            decoder.imageSize.width,
            decoder.imageSize.height,
            drawableSize.width,
            drawableSize.height,
            newZoomScale
        )
        if (newSampleSize != currentSampleSize) {
            currentTileList?.forEach { freeTile(it) }
            currentTileList = tileMap[newSampleSize]
        }
        val tileList = currentTileList
        val sourceVisibleRect = Rect(
            (visibleRect.left * newZoomScale).roundToInt(),
            (visibleRect.top * newZoomScale).roundToInt(),
            (visibleRect.right * newZoomScale).roundToInt(),
            (visibleRect.bottom * newZoomScale).roundToInt()
        )
        tileList?.forEach {
            if (it.srcRect.isIntersection(sourceVisibleRect)) {
                loadTile(it)
            } else {
                freeTile(it)
            }
        }
    }

    fun onDraw(canvas: Canvas) {
//        if (tileManager.blockList.size > 0) {
//            val saveCount = canvas.save()
//            canvas.concat(matrix)
//            for (block in tileManager.blockList) {
//                val bitmap = block.bitmap
//                if (!block.isEmpty && bitmap != null) {
//                    canvas.drawBitmap(
//                        bitmap,
//                        block.bitmapDrawSrcRect,
//                        block.drawRect,
//                        drawBlockPaint
//                    )
//                    if (isShowBlockBounds) {
//                        canvas.drawRect(block.drawRect, drawBlockRectPaint)
//                    }
//                } else if (!block.isDecodeParamEmpty) {
//                    if (isShowBlockBounds) {
//                        canvas.drawRect(block.drawRect, drawLoadingBlockRectPaint)
//                    }
//                }
//            }
//            canvas.restoreToCount(saveCount)
//        }
    }


//    private suspend fun decodeTile(tile: Tile): Bitmap? {
//        requiredMainThread()
//
//        val tileDecoder = tileDecoder
//        return if (tileDecoder != null) {
//            tileDecoder.decode(tile.key, tile)
//        } else {
//            if (decoderInitializing?.isActive != true) {
//                decoderInitializing = scope.launch {
//                    this@Tiles.tileDecoder = withContext(Dispatchers.IO) {
//                        Factory(context, imageUri, exifOrientation).create()
//                    }
//                }
//            }
//            null
//        }
//    }

    fun destroy() {
        cleanMemory()
        decoder.destroy()
    }

    fun cleanMemory() {
        tileMap.values.forEach { tileList ->
            tileList.forEach { tile ->
                // todo memory cache
                bitmapPool.free(tile.bitmap)
                tile.bitmap = null
            }
        }
    }

//    private fun getCacheKey(){
//
//
//        val memoryCacheKey: String by lazy {
//            "${imageUri}_${exifOrientation}_${srcRect.toShortString()}_${inSampleSize}"
//        }
//    }

    private fun findTileListByScale() {

    }

    private fun freeTile(tile: Tile) {

    }

    private fun loadTile(tile: Tile) {

    }
}