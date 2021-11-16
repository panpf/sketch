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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.util.Log
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.em
import com.github.panpf.sketch.SLog.Companion.emf
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SketchCallback
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.freeBitmapToPool
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.sdkSupportInBitmapForRegionDecoder
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.SketchUtils.Companion.close
import com.github.panpf.sketch.util.SketchUtils.Companion.getByteCount
import java.io.IOException
import java.io.InputStream

class ImageDecodeUtils {

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun decodeBitmap(dataSource: DataSource, options: BitmapFactory.Options): Bitmap? {
            var inputStream: InputStream? = null
            try {
                inputStream = dataSource.newInputStream()
                return BitmapFactory.decodeStream(inputStream, null, options)
            } finally {
                close(inputStream)
            }
        }

        @JvmStatic
        fun decodeRegionBitmap(
            dataSource: DataSource,
            srcRect: Rect,
            options: BitmapFactory.Options
        ): Bitmap? {
            val inputStream: InputStream = try {
                dataSource.newInputStream()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            val regionDecoder: BitmapRegionDecoder = try {
                BitmapRegionDecoder.newInstance(inputStream, false)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                close(inputStream)
            }
            val bitmap = regionDecoder.decodeRegion(srcRect, options)
            regionDecoder.recycle()
            close(inputStream)
            return bitmap
        }

        @JvmStatic
        fun decodeSuccess(
            bitmap: Bitmap,
            outWidth: Int,
            outHeight: Int,
            inSampleSize: Int,
            loadRequest: LoadRequest,
            logName: String
        ) {
            if (isLoggable(SLog.DEBUG)) {
                if (loadRequest.options.maxSize != null) {
                    val maxSize = loadRequest.options.maxSize
                    val sizeCalculator = loadRequest.configuration.sizeCalculator
                    dmf(
                        logName,
                        "Decode bitmap. originalSize=%dx%d, targetSize=%dx%d, targetSizeScale=%s, inSampleSize=%d, finalSize=%dx%d. %s",
                        outWidth,
                        outHeight,
                        maxSize!!.width,
                        maxSize.height,
                        sizeCalculator.targetSizeScale,
                        inSampleSize,
                        bitmap.width,
                        bitmap.height,
                        loadRequest.key
                    )
                } else {
                    dmf(
                        logName,
                        "Decode bitmap. bitmapSize=%dx%d. %s",
                        bitmap.width,
                        bitmap.height,
                        loadRequest.key
                    )
                }
            }
        }

        @JvmStatic
        fun decodeError(
            request: LoadRequest,
            dataSource: DataSource?,
            logName: String,
            cause: String,
            tr: Throwable?
        ) {
            if (tr != null) {
                em(logName, Log.getStackTraceString(tr))
            }
            if (dataSource is DiskCacheDataSource) {
                val diskCacheEntry = dataSource.diskCacheEntry
                val cacheFile = diskCacheEntry.file
                if (diskCacheEntry.delete()) {
                    emf(
                        logName,
                        "Decode failed. %s. Disk cache deleted. fileLength=%d. %s",
                        cause,
                        cacheFile.length(),
                        request.key,
                        tr!!
                    )
                } else {
                    emf(
                        logName,
                        "Decode failed. %s. Disk cache can not be deleted. fileLength=%d. %s",
                        cause,
                        cacheFile.length(),
                        request.key
                    )
                }
            } else if (dataSource is FileDataSource) {
                val file = dataSource.getFile(null, null)
                emf(
                    logName, "Decode failed. %s. filePath=%s, fileLength=%d. %s",
                    cause, file!!.path, if (file.exists()) file.length() else -1, request.key
                )
            } else {
                emf(logName, "Decode failed. %s. %s", cause, request.uri)
            }
        }

        /**
         * 通过异常类型以及 message 确定是不是由 inBitmap 导致的解码失败
         */
        @JvmStatic
        fun isInBitmapDecodeError(
            throwable: Throwable,
            options: BitmapFactory.Options,
            fromBitmapRegionDecoder: Boolean
        ): Boolean {
            if (fromBitmapRegionDecoder && !sdkSupportInBitmapForRegionDecoder()) {
                return false
            }
            if (throwable !is IllegalArgumentException) {
                return false
            }
            if (options.inBitmap == null) {
                return false
            }
            val message = throwable.message
            return message != null && (message == "Problem decoding into existing bitmap" || message.contains(
                "bitmap"
            ))
        }

        /**
         * 反馈 inBitmap 解码失败，并回收 inBitmap
         */
        @JvmStatic
        fun recycleInBitmapOnDecodeError(
            callback: SketchCallback,
            bitmapPool: BitmapPool,
            imageUri: String,
            imageWidth: Int,
            imageHeight: Int,
            imageMimeType: String,
            throwable: Throwable,
            decodeOptions: BitmapFactory.Options,
            fromBitmapRegionDecoder: Boolean
        ) {
            if (fromBitmapRegionDecoder && !sdkSupportInBitmapForRegionDecoder()) {
                return
            }
            emf(
                "onInBitmapException. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, " +
                        "inSampleSize=%d, inBitmapSize=%dx%d, inBitmapByteCount=%d",
                imageUri,
                imageWidth,
                imageHeight,
                imageMimeType,
                decodeOptions.inSampleSize,
                decodeOptions.inBitmap.width,
                decodeOptions.inBitmap.height,
                getByteCount(decodeOptions.inBitmap)
            )
            callback.onError(
                InBitmapDecodeException(
                    throwable,
                    imageUri,
                    imageWidth,
                    imageHeight,
                    imageMimeType,
                    decodeOptions.inSampleSize,
                    decodeOptions.inBitmap
                )
            )
            freeBitmapToPool(decodeOptions.inBitmap, bitmapPool)
            decodeOptions.inBitmap = null
        }

        /**
         * 通过异常类型以及 message 确定是不是由 srcRect 导致的解码失败
         */
        @JvmStatic
        fun isSrcRectDecodeError(
            throwable: Throwable,
            imageWidth: Int,
            imageHeight: Int,
            srcRect: Rect
        ): Boolean {
            if (throwable !is IllegalArgumentException) {
                return false
            }
            if (srcRect.left < imageWidth || srcRect.top < imageHeight || srcRect.right > imageWidth || srcRect.bottom > imageHeight) {
                return true
            }
            val message = throwable.message
            return message != null && (message == "rectangle is outside the image srcRect" || message.contains(
                "srcRect"
            ))
        }
    }
}