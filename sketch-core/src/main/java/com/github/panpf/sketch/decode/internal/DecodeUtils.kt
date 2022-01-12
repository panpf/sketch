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
package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.LoadRequest
import java.io.IOException

fun DataSource.readImageInfo(request: LoadRequest): ImageInfo {
    val boundOptions = Options().apply {
        inJustDecodeBounds = true
    }
    decodeBitmap(boundOptions)
    if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
        throw BitmapDecodeException(
            request,
            "Invalid image size. size=${boundOptions.outWidth}x${boundOptions.outHeight}, uri=${request.uriString}"
        )
    }

    val exifOrientation: Int = ExifOrientationCorrector
        .readExifOrientation(boundOptions.outMimeType, this)
    return ImageInfo(
        boundOptions.outMimeType,
        boundOptions.outWidth,
        boundOptions.outHeight,
        exifOrientation
    )
}

@Throws(IOException::class)
fun DataSource.decodeBitmap(options: BitmapFactory.Options): Bitmap? =
    newInputStream().use {
        BitmapFactory.decodeStream(it, null, options)
    }

@Throws(IOException::class)
fun DataSource.decodeRegionBitmap(srcRect: Rect, options: BitmapFactory.Options): Bitmap? =
    newInputStream().use {
        @Suppress("DEPRECATION")
        val regionDecoder = if (VERSION.SDK_INT >= VERSION_CODES.S) {
            BitmapRegionDecoder.newInstance(it)
        } else {
            BitmapRegionDecoder.newInstance(it, false)
        }
        try {
            regionDecoder?.decodeRegion(srcRect, options)
        } finally {
            regionDecoder?.recycle()
        }
    }

/**
 * 通过异常类型以及 message 确定是不是由 inBitmap 导致的解码失败
 */
fun isInBitmapError(
    throwable: Throwable,
    fromBitmapRegionDecoder: Boolean
): Boolean =
    if (!fromBitmapRegionDecoder && throwable is IllegalArgumentException) {
        val message = throwable.message.orEmpty()
        (message == "Problem decoding into existing bitmap" || message.contains("bitmap"))
    } else {
        false
    }

/**
 * 通过异常类型以及 message 确定是不是由 srcRect 导致的解码失败
 */
fun isSrcRectError(
    throwable: Throwable,
    imageWidth: Int,
    imageHeight: Int,
    srcRect: Rect
): Boolean =
    if (throwable is IllegalArgumentException) {
        if (srcRect.left >= imageWidth && srcRect.top >= imageHeight && srcRect.right <= imageWidth && srcRect.bottom <= imageHeight) {
            val message = throwable.message
            message != null && (message == "rectangle is outside the image srcRect" || message.contains(
                "srcRect"
            ))
        } else {
            true
        }
    } else {
        false
    }