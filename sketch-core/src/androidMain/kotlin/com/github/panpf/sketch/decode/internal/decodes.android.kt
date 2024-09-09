/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIC
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIF
import com.github.panpf.sketch.decode.internal.ImageFormat.JPEG
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.decode.internal.ImageFormat.WEBP
import com.github.panpf.sketch.decode.toAndroidBitmapConfig
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import okio.buffer
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor


/* ************************************** sampling ********************************************** */

/**
 * Get the maximum Bitmap size allowed by the Android platform
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testGetMaxBitmapSize
 */
actual fun getMaxBitmapSize(): Size? {
    return OpenGLTextureHelper.maxSize
        ?.let { Size(it, it) }
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampledBitmapSize
 */
actual fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String?
): Size {
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val isPNGFormat = PNG.matched(mimeType)
    val width: Int
    val height: Int
    if (isPNGFormat) {
        width = floor(widthValue).toInt()
        height = floor(heightValue).toInt()
    } else {
        width = ceil(widthValue).toInt()
        height = ceil(heightValue).toInt()
    }
    return Size(width, height)
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapRegionDecoder
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampledBitmapSizeForRegion
 */
actual fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String?,
    imageSize: Size?
): Size {
    val widthValue = regionSize.width / sampleSize.toDouble()
    val heightValue = regionSize.height / sampleSize.toDouble()
    val width: Int
    val height: Int
    val isPNGFormat = PNG.matched(mimeType)
    if (!isPNGFormat && VERSION.SDK_INT >= VERSION_CODES.N && regionSize == imageSize) {
        width = ceil(widthValue).toInt()
        height = ceil(heightValue).toInt()
    } else {
        width = floor(widthValue).toInt()
        height = floor(heightValue).toInt()
    }
    return Size(width, height)
}


/* **************************************** decode ********************************************* */

/**
 * Read image information using BitmapFactory
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfo
 */
@Throws(IOException::class)
fun DataSource.readImageInfo(): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    decodeBitmap(boundOptions)
    val mimeType = boundOptions.outMimeType.orEmpty()
    val imageSize = Size(width = boundOptions.outWidth, height = boundOptions.outHeight)
    return ImageInfo(size = imageSize, mimeType = mimeType)
}

/**
 * Read image information using BitmapFactory, and correct the image size according to the Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfoWithExifOrientation
 */
fun DataSource.readImageInfoWithExifOrientation(helper: ExifOrientationHelper? = null): ImageInfo {
    val imageInfo = readImageInfo()
    val exifOrientationHelper = if (helper != null) {
        helper
    } else {
        val exifOrientation = readExifOrientationWithMimeType(imageInfo.mimeType)
        ExifOrientationHelper(exifOrientation)
    }
    val correctedImageSize = exifOrientationHelper.applyToSize(imageInfo.size)
    return imageInfo.copy(size = correctedImageSize)
}


/**
 * Decode bitmap using BitmapFactory
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testDecodeBitmap
 */
@Throws(IOException::class)
fun DataSource.decodeBitmap(options: BitmapFactory.Options? = null): Bitmap? =
    openSource().buffer().inputStream().use {
        BitmapFactory.decodeStream(it, null, options)
    }

/**
 * Use BitmapRegionDecoder to decode part of a bitmap region
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testDecodeRegionBitmap
 */
@Throws(IOException::class)
fun DataSource.decodeRegionBitmap(
    srcRect: Rect,
    options: BitmapFactory.Options? = null
): Bitmap? =
    openSource().buffer().inputStream().use {
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
 * Create a DecodeConfig based on the parameters related to image quality in the request
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testNewDecodeConfigByQualityParams
 */
fun ImageRequest.newDecodeConfigByQualityParams(mimeType: String): DecodeConfig =
    DecodeConfig().apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed) {
            inPreferQualityOverSpeed = true
        }

        val newConfig = bitmapConfig?.toAndroidBitmapConfig(mimeType)
        if (newConfig != null) {
            inPreferredConfig = newConfig
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != null) {
            inPreferredColorSpace = colorSpace
        }
    }

/**
 * Check if the image format is supported by BitmapRegionDecoder
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testSupportBitmapRegionDecoder
 */
@SuppressLint("ObsoleteSdkInt")
fun ImageFormat.supportBitmapRegionDecoder(): Boolean =
    this == JPEG
            || this == PNG
            || this == WEBP
            || (this == HEIC && VERSION.SDK_INT >= VERSION_CODES.P)
            || (this == HEIF && VERSION.SDK_INT >= VERSION_CODES.P)