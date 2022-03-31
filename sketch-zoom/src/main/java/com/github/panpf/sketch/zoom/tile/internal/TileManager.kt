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
package com.github.panpf.sketch.zoom.tile.internal

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import androidx.annotation.MainThread
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.withSave
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.zoom.internal.getScale
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.Tiles
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.floor

class TileManager constructor(
    context: Context,
    private val imageUri: String,
    viewSize: Size,
    private val decoder: TileDecoder,
    private val tiles: Tiles,
) {

    private val logger = context.sketch.logger

    private val tileBoundsPaint: Paint by lazy {
        Paint().apply {
            style = STROKE
            strokeWidth = 1f * Resources.getSystem().displayMetrics.density
        }
    }
    private val strokeHalfWidth by lazy { (tileBoundsPaint.strokeWidth) / 2 }

    private val tileMaxSize = viewSize.let {
        Size(it.width / 2, it.height / 2)
    }
    private val tileMap: Map<Int, List<Tile>> = initializeTileMap(decoder.imageSize, tileMaxSize)
    private val bitmapPool: BitmapPool = context.sketch.bitmapPool
    private val memoryCache: MemoryCache = context.sketch.memoryCache
    private val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    private val decodeDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(4)
    private var lastTileList: List<Tile>? = null
    private var lastSampleSize: Int? = null
    private val imageVisibleRect = Rect()
    private val imageLoadRect = Rect()
    private val tileDrawRect = Rect()

    var viewSize: Size = viewSize
        internal set(value) {
            if (field != value) {
                field = value
                // todo apply view size change
            }
        }
    val tileList: List<Tile>?
        get() = lastTileList
    val imageSize = decoder.imageSize

    init {
        logger.d(Tiles.MODULE) {
            val tileMapInfoList = tileMap.keys.sortedDescending().map {
                "${it}:${tileMap[it]?.size}"
            }
            "tileMap. $tileMapInfoList"
        }
    }

    @MainThread
    fun refreshTiles(previewSize: Size, previewVisibleRect: Rect, drawMatrix: Matrix) {
        requiredMainThread()

        val zoomScale = drawMatrix.getScale().format(2)
        val sampleSize = findSampleSize(
            imageWidth = imageSize.width,
            imageHeight = imageSize.height,
            previewWidth = previewSize.width,
            previewHeight = previewSize.height,
            scale = zoomScale
        )
        if (sampleSize != lastSampleSize) {
            lastTileList?.forEach { freeTile(it) }
            lastTileList = tileMap[sampleSize]
            lastSampleSize = sampleSize
            if (lastTileList?.size == 1) {
                // Tiles are not required when the current is a minimal preview
                lastTileList = null
                lastSampleSize = null
            }
        }
        val tileList = lastTileList
        if (tileList == null) {
            logger.w(Tiles.MODULE) {
                "refreshTiles. no tileList. " +
                        "imageSize=${imageSize}, " +
                        "previewSize=$previewSize, " +
                        "previewVisibleRect=${previewVisibleRect}, " +
                        "zoomScale=$zoomScale, " +
                        "sampleSize=$lastSampleSize. " +
                        imageUri
            }
            return
        }
        resetVisibleAndLoadRect(previewSize, previewVisibleRect)

        logger.d(Tiles.MODULE) {
            "refreshTiles. successful. " +
                    "imageSize=${imageSize}, " +
                    "imageVisibleRect=$imageVisibleRect, " +
                    "imageLoadRect=$imageLoadRect, " +
                    "previewSize=$previewSize, " +
                    "previewVisibleRect=${previewVisibleRect}, " +
                    "zoomScale=$zoomScale, " +
                    "sampleSize=$lastSampleSize. " +
                    imageUri
        }
        tileList.forEach { tile ->
            if (tile.srcRect.crossWith(imageLoadRect)) {
                loadTile(tile)
            } else {
                freeTile(tile)
            }
        }
        tiles.invalidateView()
    }

    @MainThread
    fun onDraw(canvas: Canvas, previewSize: Size, previewVisibleRect: Rect, drawMatrix: Matrix) {
        requiredMainThread()

        val tileList = lastTileList
        if (tileList == null) {
            if (lastSampleSize != null) {
                logger.w(Tiles.MODULE) {
                    "onDraw. no tileList sampleSize is $lastSampleSize. $imageUri"
                }
            }
            return
        }
        resetVisibleAndLoadRect(previewSize, previewVisibleRect)
        val targetScale = (imageSize.width / previewSize.width.toFloat())
        canvas.withSave {
            canvas.concat(drawMatrix)
            tileList.forEach { tile ->
                if (tile.srcRect.crossWith(imageLoadRect)) {
                    val tileBitmap = tile.bitmap
                    val tileSrcRect = tile.srcRect
                    val tileDrawRect = tileDrawRect.apply {
                        set(
                            floor(tileSrcRect.left / targetScale).toInt(),
                            floor(tileSrcRect.top / targetScale).toInt(),
                            floor(tileSrcRect.right / targetScale).toInt(),
                            floor(tileSrcRect.bottom / targetScale).toInt()
                        )
                    }
                    if (tileBitmap != null) {
                        canvas.drawBitmap(
                            tileBitmap,
                            Rect(0, 0, tileBitmap.width, tileBitmap.height),
                            tileDrawRect,
                            null
                        )
                    }

                    if (tiles.showTileBounds) {
                        val boundsColor = when {
                            tileBitmap != null -> Color.GREEN
                            tile.loadJob?.isActive == true -> Color.YELLOW
                            else -> Color.RED
                        }
                        tileBoundsPaint.color = ColorUtils.setAlphaComponent(boundsColor, 100)
                        tileDrawRect.set(
                            floor(tileDrawRect.left + strokeHalfWidth).toInt(),
                            floor(tileDrawRect.top + strokeHalfWidth).toInt(),
                            ceil(tileDrawRect.right - strokeHalfWidth).toInt(),
                            ceil(tileDrawRect.bottom - strokeHalfWidth).toInt()
                        )
                        canvas.drawRect(tileDrawRect, tileBoundsPaint)
                    }
                }
            }
        }
    }

    @MainThread
    private fun notifyTileChanged() {
        requiredMainThread()

        tiles.onTileChangedListenerList?.forEach {
            it.onTileChanged(tiles)
        }
    }

    @MainThread
    private fun loadTile(tile: Tile) {
        requiredMainThread()

        if (tile.countBitmap != null) {
            return
        }

        val job = tile.loadJob
        if (job?.isActive == true) {
            return
        }

        val memoryCacheKey = "${imageUri}_tile_${tile.srcRect}_${tile.inSampleSize}"
        val countBitmap = memoryCache[memoryCacheKey]
        if (countBitmap != null) {
            logger.d(Tiles.MODULE) {
                "loadTile. fromMemory. $tile"
            }
            tile.countBitmap = countBitmap
            tiles.invalidateView()
            notifyTileChanged()
            return
        }

        tile.loadJob = scope.async(decodeDispatcher) {
            val bitmap = decoder.decode(tile)
            if (!isActive) {
                bitmapPool.free(bitmap)
                return@async
            }
            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    logger.d(Tiles.MODULE) {
                        "loadTile. decode. $tile"
                    }
                    val newCountBitmap = CountBitmap(
                        bitmap,
                        memoryCacheKey,
                        imageUri,
                        decoder.imageInfo,
                        decoder.exifOrientationHelper.exifOrientation,
                        null,
                        logger,
                        bitmapPool
                    )
                    memoryCache.put(memoryCacheKey, newCountBitmap)
                    tile.countBitmap = newCountBitmap
                    tiles.invalidateView()
                    notifyTileChanged()
                } else {
                    logger.e(Tiles.MODULE) {
                        "loadTile. null. $tile"
                    }
                }
            }
        }
    }

    @MainThread
    private fun freeTile(tile: Tile) {
        tile.loadJob?.run {
            if (isActive) {
                cancel()
            }
            tile.loadJob = null
        }

        tile.countBitmap?.run {
            tile.countBitmap = null
            notifyTileChanged()
            logger.d(Tiles.MODULE) {
                "freeTile. $tile"
            }
        }
    }

    fun eachTileList(
        previewSize: Size,
        previewVisibleRect: Rect,
        action: (tile: Tile, load: Boolean) -> Unit
    ) {
        val tileList = lastTileList ?: return
        resetVisibleAndLoadRect(previewSize, previewVisibleRect)
        tileList.forEach {
            action(it, it.srcRect.crossWith(imageLoadRect))
        }
    }

    private fun resetVisibleAndLoadRect(previewSize: Size, previewVisibleRect: Rect) {
        val previewScaled = imageSize.width / previewSize.width.toFloat()
        imageVisibleRect.apply {
            set(
                floor(previewVisibleRect.left * previewScaled).toInt(),
                floor(previewVisibleRect.top * previewScaled).toInt(),
                ceil(previewVisibleRect.right * previewScaled).toInt(),
                ceil(previewVisibleRect.bottom * previewScaled).toInt()
            )
        }
        // Increase the visible area as the loading area,
        // this preloads tiles around the visible area,
        // the user will no longer feel the loading process while sliding slowly
        imageLoadRect.apply {
            set(
                imageVisibleRect.left - tileMaxSize.width / 2,
                imageVisibleRect.top - tileMaxSize.height / 2,
                imageVisibleRect.right + tileMaxSize.width / 2,
                imageVisibleRect.bottom + tileMaxSize.height / 2
            )
        }
    }

    @MainThread
    fun freeAllTile() {
        requiredMainThread()

        tileMap.values.forEach { tileList ->
            tileList.forEach { tile ->
                freeTile(tile)
            }
        }
        tiles.invalidateView()
    }

    @MainThread
    fun destroy() {
        requiredMainThread()

        freeAllTile()
        decoder.destroy()
    }
}