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
import androidx.annotation.Px
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.ImageFormat.HEIC
import com.github.panpf.sketch.ImageFormat.HEIF
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL
 */
fun limitedOpenGLTextureMaxSize(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    inSampleSize: Int
): Int {
    val openGLTextureMaxSize = OpenGLTextureHelper.maxSize ?: return inSampleSize
    var finalInSampleSize = inSampleSize.coerceAtLeast(1)
    while ((calculateSamplingSize(imageWidth, finalInSampleSize) > openGLTextureMaxSize)
        || (calculateSamplingSize(imageHeight, finalInSampleSize) > openGLTextureMaxSize)
    ) {
        finalInSampleSize *= 2
    }
    return finalInSampleSize
}

/**
 * Calculate the inSampleSize for [BitmapFactory.Options]
 *
 * @param targetScale Margin of error is allowed for better resolution
 */
fun calculateInSampleSize(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    @Px targetWidth: Int,
    @Px targetHeight: Int,
): Int {
    val newTargetWidth: Int = targetWidth
    val newTargetHeight: Int = targetHeight

    val targetScale = 1.1f
    val targetPixels = newTargetWidth.times(newTargetHeight).times(targetScale).roundToInt()
    var inSampleSize = 1
    while (true) {
        val sampledWidth = calculateSamplingSize(imageWidth, inSampleSize)
        val sampledHeight = calculateSamplingSize(imageHeight, inSampleSize)
        if (sampledWidth.times(sampledHeight) <= targetPixels) {
            break
        } else {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

fun calculateSamplingSize(value1: Int, inSampleSize: Int): Int {
    return ceil((value1 / inSampleSize.toFloat()).toDouble()).toInt()
}

fun calculateSamplingSizeForRegion(value1: Int, inSampleSize: Int): Int {
    return floor((value1 / inSampleSize.toFloat()).toDouble()).toInt()
}

fun DataSource.readImageInfoWithBitmapFactory(): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    decodeBitmapWithBitmapFactory(boundOptions)
    return ImageInfo(
        width = boundOptions.outWidth,
        height = boundOptions.outHeight,
        mimeType = boundOptions.outMimeType.orEmpty()
    )
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
            || (VERSION.SDK_INT >= VERSION_CODES.P && (this == HEIF || this == HEIC))

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