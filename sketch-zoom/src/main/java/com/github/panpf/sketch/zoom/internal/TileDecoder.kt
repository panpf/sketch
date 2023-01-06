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
package com.github.panpf.sketch.zoom.internal

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
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.freeBitmap
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.decode.internal.setInBitmapForRegion
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.Tile
import java.util.LinkedList

internal class TileDecoder internal constructor(
    sketch: Sketch,
    val imageUri: String,
    val imageInfo: ImageInfo,
    private val disallowReuseBitmap: Boolean,
    private val dataSource: DataSource,
) {
    private val logger: Logger = sketch.logger
    private val bitmapPool: BitmapPool = sketch.bitmapPool
    private val decoderPool = LinkedList<BitmapRegionDecoder>()
    private val exifOrientationHelper: ExifOrientationHelper =
        ExifOrientationHelper(imageInfo.exifOrientation)
    private var _destroyed: Boolean = false
    private val imageSize: Size = Size(imageInfo.width, imageInfo.height)
    private val addedImageSize: Size by lazy { exifOrientationHelper.addToSize(imageSize) }

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
        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        }
        bitmapPool.setInBitmapForRegion(
            options = decodeOptions,
            regionSize = Size(newSrcRect.width(), newSrcRect.height()),
            imageMimeType = imageInfo.mimeType,
            imageSize = addedImageSize,
            disallowReuseBitmap = disallowReuseBitmap,
            caller = "tile:decodeRegion"
        )
        logger.d(SubsamplingHelper.MODULE) {
            "decodeRegion. inBitmap=${decodeOptions.inBitmap?.logString}. '$imageUri'"
        }

        return try {
            regionDecoder.decodeRegion(newSrcRect, decodeOptions)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                logger.e(SubsamplingHelper.MODULE, throwable) {
                    "decodeRegion. Bitmap region decode inBitmap error. '$imageUri'"
                }

                bitmapPool.freeBitmap(
                    bitmap = inBitmap,
                    disallowReuseBitmap = disallowReuseBitmap,
                    caller = "tile:decodeRegion:error"
                )
                logger.d(SubsamplingHelper.MODULE) {
                    "decodeRegion. freeBitmap. inBitmap error. bitmap=${inBitmap.logString}. '$imageUri'"
                }

                decodeOptions.inBitmap = null
                try {
                    regionDecoder.decodeRegion(newSrcRect, decodeOptions)
                } catch (throwable1: Throwable) {
                    throwable1.printStackTrace()
                    logger.e(SubsamplingHelper.MODULE, throwable) {
                        "decodeRegion. Bitmap region decode error. srcRect=${newSrcRect}. '$imageUri'"
                    }
                    null
                }
            } else if (isSrcRectError(throwable)) {
                logger.e(SubsamplingHelper.MODULE, throwable) {
                    "decodeRegion. Bitmap region decode srcRect error. imageSize=$imageSize, srcRect=$newSrcRect, inSampleSize=${decodeOptions.inSampleSize}. '$imageUri'"
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

        val newBitmap = exifOrientationHelper.applyToBitmap(bitmap, bitmapPool, disallowReuseBitmap)
        return if (newBitmap != null && newBitmap != bitmap) {
            bitmapPool.freeBitmap(bitmap, disallowReuseBitmap, "tile:applyExifOrientation")
            logger.d(SubsamplingHelper.MODULE) {
                "applyExifOrientation. freeBitmap. bitmap=${bitmap.logString}. '$imageUri'"
            }
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

        var bitmapRegionDecoder: BitmapRegionDecoder? = synchronized(decoderPool) {
            decoderPool.poll()
        }
        if (bitmapRegionDecoder == null && dataSource is BasedStreamDataSource) {
            bitmapRegionDecoder = dataSource.newInputStream().buffered().use {
                if (VERSION.SDK_INT >= VERSION_CODES.S) {
                    BitmapRegionDecoder.newInstance(it)
                } else {
                    @Suppress("DEPRECATION")
                    BitmapRegionDecoder.newInstance(it, false)
                }
            }
        }
        if (bitmapRegionDecoder == null) {
            return null
        }

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