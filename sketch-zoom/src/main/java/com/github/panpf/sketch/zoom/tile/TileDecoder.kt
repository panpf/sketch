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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.LinkedList

class TileDecoder private constructor(
    private val context: Context,
    val imageUri: String,
    val imageSize: Size,
    val exifOrientationHelper: ExifOrientationHelper,
    val dataSource: DataSource,
) {

    companion object {
        private const val NAME = "BlockDecoder"
    }

    private val logger: Logger = context.sketch.logger
    private var _destroyed: Boolean = false
    private val bitmapPool: BitmapPool = context.sketch.bitmapPool
    private val decoderPool: BitmapRegionDecoderPool = BitmapRegionDecoderPool()
    private var disableInBitmap: Boolean = false
    private val decodeDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(4)

    val destroyed: Boolean
        get() = _destroyed

    private fun createBitmapRegionDecoder(): BitmapRegionDecoder? =
        dataSource.newInputStream().use {
            if (VERSION.SDK_INT >= VERSION_CODES.S) {
                BitmapRegionDecoder.newInstance(it)
            } else {
                @Suppress("DEPRECATION")
                BitmapRegionDecoder.newInstance(it, false)
            }
        }

    @MainThread
    suspend fun decode(key: Int, tile: Tile): Bitmap? {
        requiredMainThread()
        if (_destroyed || tile.isExpired(key)) return null

        return withContext(decodeDispatcher) {
            if (tile.isExpired(key)) {
                return@withContext null
            }

            val bitmapRegionDecoder = decoderPool.poll()
                ?: createBitmapRegionDecoder()
                ?: return@withContext null

            val bitmap = decodeRegion(bitmapRegionDecoder, tile.srcRect, tile.inSampleSize)?.let {
                applyExifOrientation(it)
            }
            decoderPool.put(bitmapRegionDecoder)
            if (tile.isExpired(key)) {
                bitmapPool.free(bitmap)
                return@withContext null
            }

            bitmap
        }
    }

    @WorkerThread
    private fun decodeRegion(
        regionDecoder: BitmapRegionDecoder,
        srcRect: Rect,
        inSampleSize: Int
    ): Bitmap? {
        val imageSize = imageSize
        val newSrcRect = exifOrientationHelper.addToRect(srcRect, imageSize)
        val options = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
            if (!disableInBitmap) {
                bitmapPool.setInBitmapForRegionDecoder(
                    this, newSrcRect.width(), newSrcRect.height()
                )
            }
        }

        return try {
            regionDecoder.decodeRegion(newSrcRect, options)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            val inBitmap = options.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                disableInBitmap = true
                logger.e(NAME, throwable, "Bitmap region decode inBitmap error. $imageUri")

                options.inBitmap = null
                bitmapPool.free(inBitmap)
                try {
                    regionDecoder.decodeRegion(newSrcRect, options)
                } catch (throwable1: Throwable) {
                    throwable1.printStackTrace()
                    logger.e(NAME, throwable) {
                        "Bitmap region decode error. srcRect=${newSrcRect}. $imageUri"
                    }
                    null
                }
            } else if (isSrcRectError(throwable)) {
                logger.e(NAME, throwable) {
                    "Bitmap region decode srcRect error. imageSize=$imageSize, srcRect=$newSrcRect, inSampleSize=${options.inSampleSize}. $imageUri"
                }
                null
            } else {
                null
            }
        }
    }

    @WorkerThread
    private fun applyExifOrientation(bitmap: Bitmap): Bitmap {
        val newBitmap = exifOrientationHelper.applyToBitmap(bitmap, bitmapPool)
        return if (newBitmap != null && newBitmap != bitmap) {
            bitmapPool.free(bitmap)
            newBitmap
        } else {
            bitmap
        }
    }

    fun destroy() {
        _destroyed = true
        decoderPool.destroy()
    }

    private class BitmapRegionDecoderPool {

        private val pool = LinkedList<BitmapRegionDecoder>()
        private var destroyed = false

        fun poll(): BitmapRegionDecoder? =
            synchronized(this) {
                if (destroyed) {
                    null
                } else {
                    pool.poll()
                }
            }

        fun put(decoder: BitmapRegionDecoder) {
            synchronized(this) {
                if (destroyed) {
                    decoder.recycle()
                } else {
                    pool.add(decoder)
                }
            }
        }

        fun destroy() {
            synchronized(this) {
                destroyed = true
                pool.forEach {
                    it.recycle()
                }
                pool.clear()
            }
        }
    }

    class Factory(val context: Context, val imageUri: String, val exifOrientation: Int) {

        @WorkerThread
        fun create(): TileDecoder? {
            requiredWorkThread()

            val sketch = context.sketch
            val request = LoadRequest(context, imageUri)
            val fetch = sketch.componentRegistry.newFetcher(sketch, request)
            val fetchResult = runBlocking {
                fetch.fetch()
            }

            val imageInfo = fetchResult.dataSource.readImageInfoWithBitmapFactoryOrNull()
                ?: throw Exception("Unsupported image format.  $imageUri")
            val exifOrientationHelper = ExifOrientationHelper(exifOrientation)
            val imageSize =
                exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
            val imageFormat = ImageFormat.valueOfMimeType(imageInfo.mimeType)
            if (imageFormat?.supportBitmapRegionDecoder() != true) {
                return null
            }

            return TileDecoder(
                context = context,
                imageUri = imageUri,
                imageSize = imageSize,
                exifOrientationHelper = exifOrientationHelper,
                dataSource = fetchResult.dataSource,
            )
        }
    }
}