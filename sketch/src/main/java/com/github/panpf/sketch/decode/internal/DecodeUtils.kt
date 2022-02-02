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
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.ImageFormat.HEIF
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import java.io.IOException

fun DataSource.readImageInfoWithBitmapFactory(): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    decodeBitmapWithBitmapFactory(boundOptions)
    val mimeType = boundOptions.outMimeType ?: ""
    val exifOrientation: Int = readExifOrientationWithMimeType(mimeType)
    return ImageInfo(boundOptions.outWidth, boundOptions.outHeight, mimeType, exifOrientation)
}

fun DataSource.readImageInfoWithBitmapFactoryOrThrow(): ImageInfo {
    val imageInfo = readImageInfoWithBitmapFactory()
    val width = imageInfo.width
    val height = imageInfo.height
    val mimeType = imageInfo.mimeType
    if (width <= 0 || height <= 0 || mimeType.isEmpty()) {
        throw Exception("Invalid image, size=${width}x${height}, imageType='${mimeType}'")
    }
    return imageInfo
}

fun DataSource.readImageInfoWithBitmapFactoryOrNull(): ImageInfo? =
    readImageInfoWithBitmapFactory().takeIf {
        it.width > 0 && it.height > 0 && it.mimeType.isNotEmpty()
    }

@Throws(IOException::class)
fun DataSource.decodeBitmapWithBitmapFactory(options: BitmapFactory.Options? = null): Bitmap? =
    newInputStream().use {
        BitmapFactory.decodeStream(it, null, options)
    }

fun ImageFormat.supportBitmapRegionDecoder(): Boolean =
    this == ImageFormat.JPEG
            || this == ImageFormat.PNG
            || this == ImageFormat.WEBP
            || (VERSION.SDK_INT >= VERSION_CODES.P && this == HEIF)

@Throws(IOException::class)
fun DataSource.decodeRegionBitmap(srcRect: Rect, options: BitmapFactory.Options? = null): Bitmap? =
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

fun isInBitmapError(throwable: Throwable): Boolean =
    if (throwable is IllegalArgumentException) {
        val message = throwable.message.orEmpty()
        (message == "Problem decoding into existing bitmap" || message.contains("bitmap"))
    } else {
        false
    }

fun isSrcRectError(throwable: Throwable): Boolean =
    if (throwable is IllegalArgumentException) {
        val message = throwable.message.orEmpty()
        message == "rectangle is outside the image srcRect" || message.contains("srcRect")
    } else {
        false
    }