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
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIC
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIF
import com.github.panpf.sketch.decode.internal.ImageFormat.JPEG
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.decode.internal.ImageFormat.WEBP
import com.github.panpf.sketch.decode.toBitmapOptions
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toAndroidRect
import okio.buffer
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor

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

/**
 * Read image information using BitmapFactory. Ignore the Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfoWithExifOrientation
 */
@Throws(IOException::class)
fun DataSource.readImageInfoWithIgnoreExifOrientation(): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, this@apply)
        }
    }
    val mimeType = boundOptions.outMimeType.orEmpty()
    val imageSize = Size(width = boundOptions.outWidth, height = boundOptions.outHeight)
    if (imageSize.isEmpty) {
        throw ImageInvalidException("Invalid image. width or height is 0. $imageSize")
    }
    return ImageInfo(size = imageSize, mimeType = mimeType)
}

/**
 * Read image information using BitmapFactory. Parse Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfo
 */
fun DataSource.readImageInfo(helper: ExifOrientationHelper?): ImageInfo {
    val imageInfo = readImageInfoWithIgnoreExifOrientation()
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
 * Decode image width, height, MIME type. Parse Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfo
 */
actual fun DataSource.readImageInfo(): ImageInfo = readImageInfo(null)

/**
 * Decode bitmap using BitmapFactory. Parse Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testDecode
 */
@Throws(IOException::class)
fun DataSource.decode(
    config: DecodeConfig? = null,
    exifOrientationHelper: ExifOrientationHelper? = null
): Bitmap = openSource().buffer().inputStream().use {
    val options = config?.toBitmapOptions()
    val bitmap = BitmapFactory.decodeStream(it, null, options)
        ?: throw ImageInvalidException("Invalid image. decode return null")
    val exifOrientationHelper1 =
        exifOrientationHelper ?: ExifOrientationHelper(readExifOrientation())
    val correctedImage = exifOrientationHelper1.applyToBitmap(bitmap) ?: bitmap
    correctedImage
}

/**
 * Use BitmapRegionDecoder to decode part of a bitmap region. Parse Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testDecodeRegion
 */
@Throws(IOException::class)
fun DataSource.decodeRegion(
    srcRect: Rect,
    config: DecodeConfig? = null,
    imageInfo: ImageInfo? = null,
    exifOrientationHelper: ExifOrientationHelper? = null
): Bitmap = openSource().buffer().inputStream().use {
    @Suppress("DEPRECATION")
    val regionDecoder = if (VERSION.SDK_INT >= VERSION_CODES.S) {
        BitmapRegionDecoder.newInstance(it)
    } else {
        BitmapRegionDecoder.newInstance(it, false)
    }
    val imageInfo1 = imageInfo ?: readImageInfo(exifOrientationHelper)
    val exifOrientationHelper1 =
        exifOrientationHelper ?: ExifOrientationHelper(readExifOrientation())
    val originalRegion =
        exifOrientationHelper1.applyToRect(srcRect, imageInfo1.size, reverse = true)
    val bitmapOptions = config?.toBitmapOptions()
    val regionBitmap = try {
        regionDecoder?.decodeRegion(originalRegion.toAndroidRect(), bitmapOptions)
            ?: throw ImageInvalidException("Invalid image. decode return null")
    } finally {
        regionDecoder?.recycle()
    }
    val correctedRegionImage = exifOrientationHelper1.applyToBitmap(regionBitmap) ?: regionBitmap
    correctedRegionImage
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