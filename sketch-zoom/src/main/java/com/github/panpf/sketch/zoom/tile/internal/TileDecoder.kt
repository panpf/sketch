/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.freeBitmap
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.decode.internal.setInBitmapForRegion
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.internal.requiredMainThread
import com.github.panpf.sketch.zoom.internal.requiredWorkThread
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.Tiles
import kotlinx.coroutines.runBlocking
import java.util.LinkedList

@WorkerThread
fun createTileDecoder(
    context: Context,
    sketch: Sketch,
    imageUri: String,
    ignoreExifOrientation: Boolean
): TileDecoder? {
    requiredWorkThread()

    val request = LoadRequest(context, imageUri)
    val fetch = sketch.components.newFetcher(request)
    val fetchResult = runBlocking {
        fetch.fetch()
    }

    val imageInfo =
        fetchResult.dataSource.readImageInfoWithBitmapFactoryOrNull(ignoreExifOrientation)
            ?: throw Exception("Unsupported image format.  $imageUri")
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
    val imageSize =
        exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
    val imageFormat = ImageFormat.parseMimeType(imageInfo.mimeType)
    if (imageFormat?.supportBitmapRegionDecoder() != true) {
        return null
    }

    return TileDecoder(
        sketch = sketch,
        imageUri = imageUri,
        imageInfo = ImageInfo(
            imageSize.width,
            imageSize.height,
            imageInfo.mimeType,
            imageInfo.exifOrientation
        ),
        exifOrientationHelper = exifOrientationHelper,
        dataSource = fetchResult.dataSource,
    )
}

class TileDecoder internal constructor(
    sketch: Sketch,
    val imageUri: String,
    val imageInfo: ImageInfo,
    val exifOrientationHelper: ExifOrientationHelper,
    val dataSource: DataSource,
) {

    private val logger: Logger = sketch.logger
    private val bitmapPool: BitmapPool = sketch.bitmapPool
    private val decoderPool = LinkedList<BitmapRegionDecoder>()
    private var _destroyed: Boolean = false
    private var disableInBitmap: Boolean = false
    private val addedImageSize: Size by lazy { exifOrientationHelper.addToSize(imageSize) }

    val imageSize: Size by lazy {
        Size(imageInfo.width, imageInfo.height)
    }
    @Suppress("MemberVisibilityCanBePrivate")
    val destroyed: Boolean
        get() = _destroyed

    @WorkerThread
    fun decode(tile: Tile): Bitmap? {
        requiredWorkThread()

        if (_destroyed) return null
        return useDecoder { decoder ->
            decodeRegion(decoder, tile.srcRect, tile.inSampleSize)?.let {
                applyExifOrientation(it)
            }
        }
    }

    @WorkerThread
    private fun decodeRegion(
        regionDecoder: BitmapRegionDecoder,
        srcRect: Rect,
        inSampleSize: Int
    ): Bitmap? {
        requiredWorkThread()

        val imageSize = imageSize
        val newSrcRect = exifOrientationHelper.addToRect(srcRect, imageSize)
        val options = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        }
        if (!disableInBitmap) {
            setInBitmapForRegion(
                bitmapPool = bitmapPool,
                logger = logger,
                options = options,
                regionSize = Size(newSrcRect.width(), newSrcRect.height()),
                imageMimeType = imageInfo.mimeType,
                imageSize = addedImageSize
            )
        }

        return try {
            regionDecoder.decodeRegion(newSrcRect, options)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            val inBitmap = options.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                disableInBitmap = true
                logger.e(
                    Tiles.MODULE,
                    throwable,
                    "decodeRegion. Bitmap region decode inBitmap error. $imageUri"
                )

                options.inBitmap = null
                freeBitmap(bitmapPool, logger, inBitmap, "tile:decodeRegion:error")
                try {
                    regionDecoder.decodeRegion(newSrcRect, options)
                } catch (throwable1: Throwable) {
                    throwable1.printStackTrace()
                    logger.e(Tiles.MODULE, throwable) {
                        "decodeRegion. Bitmap region decode error. srcRect=${newSrcRect}. $imageUri"
                    }
                    null
                }
            } else if (isSrcRectError(throwable)) {
                logger.e(Tiles.MODULE, throwable) {
                    "decodeRegion. Bitmap region decode srcRect error. imageSize=$imageSize, srcRect=$newSrcRect, inSampleSize=${options.inSampleSize}. $imageUri"
                }
                null
            } else {
                null
            }
        }
    }

    @WorkerThread
    private fun applyExifOrientation(bitmap: Bitmap): Bitmap {
        requiredWorkThread()

        val newBitmap = exifOrientationHelper.applyToBitmap(bitmap, bitmapPool)
        return if (newBitmap != null && newBitmap != bitmap) {
            freeBitmap(bitmapPool, logger, bitmap, "tile:applyExifOrientation")
            newBitmap
        } else {
            bitmap
        }
    }

    @MainThread
    fun destroy() {
        requiredMainThread()

        synchronized(decoderPool) {
            _destroyed = true
            decoderPool.forEach {
                it.recycle()
            }
            decoderPool.clear()
        }
    }

    @WorkerThread
    private fun useDecoder(block: (decoder: BitmapRegionDecoder) -> Bitmap?): Bitmap? {
        requiredWorkThread()

        synchronized(decoderPool) {
            if (destroyed) {
                return null
            }
        }

        val bitmapRegionDecoder = synchronized(decoderPool) {
            decoderPool.poll()
        } ?: dataSource.newInputStream().buffered().use {
            if (VERSION.SDK_INT >= VERSION_CODES.S) {
                BitmapRegionDecoder.newInstance(it)
            } else {
                @Suppress("DEPRECATION")
                BitmapRegionDecoder.newInstance(it, false)
            }
        } ?: return null

        val bitmap = block(bitmapRegionDecoder)

        synchronized(decoderPool) {
            if (destroyed) {
                bitmapRegionDecoder.recycle()
            } else {
                decoderPool.add(bitmapRegionDecoder)
            }
        }

        return bitmap
    }
}