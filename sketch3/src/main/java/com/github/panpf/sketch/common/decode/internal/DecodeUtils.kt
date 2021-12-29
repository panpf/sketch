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
package com.github.panpf.sketch.common.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.common.cache.BitmapPoolHelper
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.util.byteCountCompat
import java.io.IOException

@Throws(IOException::class)
fun DataSource.decodeBitmap(options: BitmapFactory.Options): Bitmap? {
    return newInputStream().use {
        BitmapFactory.decodeStream(it, null, options)
    }
}

@Throws(IOException::class)
fun DataSource.decodeRegionBitmap(srcRect: Rect, options: BitmapFactory.Options): Bitmap? {
    return newInputStream().use {
        val regionDecoder = BitmapRegionDecoder.newInstance(it, false)
        try {
            regionDecoder.decodeRegion(srcRect, options)
        } finally {
            regionDecoder.recycle()
        }
    }
}

//fun decodeSuccess(
//    bitmap: Bitmap,
//    outWidth: Int,
//    outHeight: Int,
//    inSampleSize: Int,
//    loadRequest: LoadRequest,
//    logName: String
//) {
//    if (isLoggable(SLog.DEBUG)) {
//        if (loadRequest.options.maxSize != null) {
//            val maxSize = loadRequest.options.maxSize
//            val sizeCalculator = loadRequest.configuration.sizeCalculator
//            dmf(
//                logName,
//                "Decode bitmap. originalSize=%dx%d, targetSize=%dx%d, targetSizeScale=%s, inSampleSize=%d, finalSize=%dx%d. %s",
//                outWidth,
//                outHeight,
//                maxSize!!.width,
//                maxSize.height,
//                sizeCalculator.targetSizeScale,
//                inSampleSize,
//                bitmap.width,
//                bitmap.height,
//                loadRequest.key
//            )
//        } else {
//            dmf(
//                logName,
//                "Decode bitmap. bitmapSize=%dx%d. %s",
//                bitmap.width,
//                bitmap.height,
//                loadRequest.key
//            )
//        }
//    }
//}
//
//fun decodeError(
//    request: LoadableRequest,
//    dataSource: DataSource?,
//    logName: String,
//    cause: String,
//    tr: Throwable?
//) {
//    if (tr != null) {
//        SLog.em(logName, Log.getStackTraceString(tr))
//    }
//    if (dataSource is DiskCacheDataSource) {
//        val diskCacheEntry = dataSource.diskCacheEntry
//        val cacheFile = diskCacheEntry.file
//        if (diskCacheEntry.delete()) {
//            SLog.emf(
//                logName,
//                "Decode failed. %s. Disk cache deleted. fileLength=%d. %s",
//                cause,
//                cacheFile.length(),
//                request.key,
//                tr!!
//            )
//        } else {
//            SLog.emf(
//                logName,
//                "Decode failed. %s. Disk cache can not be deleted. fileLength=%d. %s",
//                cause,
//                cacheFile.length(),
//                request.key
//            )
//        }
//    } else if (dataSource is FileDataSource) {
//        val file = dataSource.getFile(null, null)
//        SLog.emf(
//            logName, "Decode failed. %s. filePath=%s, fileLength=%d. %s",
//            cause, file!!.path, if (file.exists()) file.length() else -1, request.key
//        )
//    } else {
//        SLog.emf(logName, "Decode failed. %s. %s", cause, request.uri)
//    }
//}

/**
 * 通过异常类型以及 message 确定是不是由 inBitmap 导致的解码失败
 */
fun isInBitmapError(
    throwable: Throwable,
    fromBitmapRegionDecoder: Boolean
): Boolean {
    if (!fromBitmapRegionDecoder && throwable is IllegalArgumentException) {
        val message = throwable.message.orEmpty()
        return message == "Problem decoding into existing bitmap"
                || message.contains("bitmap")
    }
    return false
}

/**
 * 反馈 inBitmap 解码失败，并回收 inBitmap
 */
fun recycleInBitmapOnDecodeError(
    bitmapPoolHelper: BitmapPoolHelper,
    imageUri: String,
    imageWidth: Int,
    imageHeight: Int,
    imageMimeType: String,
    throwable: Throwable,
    decodeOptions: BitmapFactory.Options,
    fromBitmapRegionDecoder: Boolean
) {
    if (fromBitmapRegionDecoder) {
        return
    }
    SLog.emf(
        "onInBitmapException. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, " +
                "inSampleSize=%d, inBitmapSize=%dx%d, inBitmapByteCount=%d",
        imageUri,
        imageWidth,
        imageHeight,
        imageMimeType,
        decodeOptions.inSampleSize,
        decodeOptions.inBitmap.width,
        decodeOptions.inBitmap.height,
        decodeOptions.inBitmap.byteCountCompat
    )
//    callback.onError(
//        InBitmapDecodeException(
//            throwable,
//            imageUri,
//            imageWidth,
//            imageHeight,
//            imageMimeType,
//            decodeOptions.inSampleSize,
//            decodeOptions.inBitmap
//        )
//    )
    bitmapPoolHelper.freeBitmapToPool(decodeOptions.inBitmap)
    decodeOptions.inBitmap = null
}

/**
 * 通过异常类型以及 message 确定是不是由 srcRect 导致的解码失败
 */
fun isSrcRectError(
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