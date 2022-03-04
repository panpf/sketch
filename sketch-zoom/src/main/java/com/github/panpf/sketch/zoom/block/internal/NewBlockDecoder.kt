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
package com.github.panpf.sketch.zoom.block.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.zoom.block.Block
import kotlinx.coroutines.runBlocking

class NewBlockDecoder private constructor(
    private val context: Context,
    val imageUri: String,
    val imageSize: Size,
    private var regionDecoder: BitmapRegionDecoder?,
    private val exifOrientationHelper: ExifOrientationHelper,
) {

    companion object {
        private const val NAME = "BlockDecoder"
    }

    private val logger = context.sketch.logger
    private val bitmapPool = context.sketch.bitmapPool
    private var disableInBitmap = false

    @WorkerThread
    fun decodeBlock(key: Int, block: Block): Bitmap? {
        if (block.isDecodeParamEmpty) {
            return null
        }
        val bitmap = synchronized(this) {
            val regionDecoder = regionDecoder ?: return null
            decodeRegion(regionDecoder, block.srcRect, block.inSampleSize) ?: return null
        }
        if (block.isExpired(key)) {
            bitmapPool.free(bitmap)
            return null
        }
        return applyExifOrientation(bitmap)
    }

    @WorkerThread
    private fun decodeRegion(
        regionDecoder: BitmapRegionDecoder,
        blockSrcRect: Rect,
        blockInSampleSize: Int
    ): Bitmap? {
        val imageSize = imageSize
        val srcRect = exifOrientationHelper.addToRect(blockSrcRect, imageSize)
        val options = BitmapFactory.Options().apply {
            this.inSampleSize = blockInSampleSize
            if (!disableInBitmap) {
                bitmapPool.setInBitmapForRegionDecoder(this, srcRect.width(), srcRect.height())
            }
        }

        return try {
            regionDecoder.decodeRegion(srcRect, options)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            val inBitmap = options.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                disableInBitmap = true
                logger.e(NAME, throwable, "Bitmap region decode inBitmap error. $imageUri")

                options.inBitmap = null
                bitmapPool.free(inBitmap)
                try {
                    regionDecoder.decodeRegion(srcRect, options)
                } catch (throwable1: Throwable) {
                    throwable1.printStackTrace()
                    logger.e(NAME, throwable) {
                        "Bitmap region decode error. srcRect=${srcRect}. $imageUri"
                    }
                    null
                }
            } else if (isSrcRectError(throwable)) {
                logger.e(NAME, throwable) {
                    "Bitmap region decode srcRect error. imageSize=$imageSize, srcRect=$srcRect, inSampleSize=${options.inSampleSize}. $imageUri"
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

    fun recycle() {
        synchronized(this) {
            regionDecoder?.recycle()
            regionDecoder = null
        }
    }

    class Factory {

        @WorkerThread
        fun create(context: Context, imageUri: String, exifOrientation: Int): NewBlockDecoder? {
            requiredWorkThread()

            val sketch = context.sketch
            val request = LoadRequest(context, imageUri)
            val fetch = sketch.componentRegistry.newFetcher(sketch, request)
            val fetchResult = runBlocking {
                fetch.fetch()
            }
            val regionDecoder: BitmapRegionDecoder = fetchResult.dataSource.newInputStream().use {
                if (VERSION.SDK_INT >= VERSION_CODES.S) {
                    BitmapRegionDecoder.newInstance(it)
                } else {
                    @Suppress("DEPRECATION")
                    BitmapRegionDecoder.newInstance(it, false)
                }
            } ?: return null

            val imageInfo = fetchResult.dataSource.readImageInfoWithBitmapFactoryOrNull()
                ?: throw Exception("Unsupported image format.  $imageUri")
            val exifOrientationHelper = ExifOrientationHelper(exifOrientation)
            val imageSize =
                exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
            return NewBlockDecoder(
                context = context,
                imageUri = imageUri,
                imageSize = imageSize,
                regionDecoder = regionDecoder,
                exifOrientationHelper = exifOrientationHelper
            )
        }
    }
}