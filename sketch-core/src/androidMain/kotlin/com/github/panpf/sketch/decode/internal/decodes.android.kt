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
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.toBitmapOptions
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toAndroidRect
import okio.buffer
import java.io.IOException
import kotlin.math.ceil

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
 * Rough calculate the size of the sampled Bitmap, only suitable for calculating sampleSize, support for BitmapFactory and ImageDecoder
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampledBitmapSize
 */
actual fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String?
): Size {
    // Because it cannot be calculated accurately, ceil is always used.
    // Different Android versions and different image formats will have different results.
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val width: Int = ceil(widthValue).toInt()
    val height: Int = ceil(heightValue).toInt()
    return Size(width, height)
//    val widthValue = imageSize.width / sampleSize.toDouble()
//    val heightValue = imageSize.height / sampleSize.toDouble()
//    val isPNGFormat = ImageFormat.PNG.matched(mimeType)
//    val width: Int
//    val height: Int
//    if (isPNGFormat) {
//        width = floor(widthValue).toInt()
//        height = floor(heightValue).toInt()
//    } else {
//        width = ceil(widthValue).toInt()
//        height = ceil(heightValue).toInt()
//    }
//    return Size(width, height)
//    val floor = ImageFormat.PNG.matched(mimeType)
//        || ImageFormat.BMP.matched(mimeType)
//        || (ImageFormat.WEBP.matched(mimeType) && VERSION.SDK_INT <= VERSION_CODES.M)
//        || ImageFormat.GIF.matched(mimeType)
//    val widthValue = imageSize.width / sampleSize.toDouble()
//    val heightValue = imageSize.height / sampleSize.toDouble()
//    val width: Int = widthValue.toInt(ceilOrFloor = !floor)
//    val height: Int = heightValue.toInt(ceilOrFloor = !floor)
//    return Size(width = width, height = height)
}

/**
 * Rough calculate the size of the sampled Bitmap, only suitable for calculating sampleSize, support for BitmapRegionDecoder
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampledBitmapSizeForRegion
 */
actual fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String?,
    imageSize: Size?
): Size {
    // Because it cannot be calculated accurately, ceil is always used.
    // Different Android versions and different image formats will have different results.
    // For example, in a gif image, the rect width is 400, the returned bitmap width is 401
    val widthValue = regionSize.width / sampleSize.toDouble()
    val heightValue = regionSize.height / sampleSize.toDouble()
    val width: Int = ceil(widthValue).toInt()
    val height: Int = ceil(heightValue).toInt()
    return Size(width, height)
//    val widthValue = regionSize.width / sampleSize.toDouble()
//    val heightValue = regionSize.height / sampleSize.toDouble()
//    val width: Int
//    val height: Int
//    val isPNGFormat = ImageFormat.PNG.matched(mimeType)
//    if (!isPNGFormat && VERSION.SDK_INT >= VERSION_CODES.N && regionSize == imageSize) {
//        width = ceil(widthValue).toInt()
//        height = ceil(heightValue).toInt()
//    } else {
//        width = floor(widthValue).toInt()
//        height = floor(heightValue).toInt()
//    }
//    return Size(width, height)
//    val widthValue = regionSize.width / sampleSize.toDouble()
//    val heightValue = regionSize.height / sampleSize.toDouble()
//    val width: Int
//    val height: Int
//    if (ImageFormat.WEBP.matched(mimeType)) {
//        if (VERSION.SDK_INT >= VERSION_CODES.N) {
//            width = floor(widthValue).toInt()
//            height = ceil(heightValue).toInt()
//        } else {
//            width = floor(widthValue).toInt()
//            height = floor(heightValue).toInt()
//        }
//    } else if (!ImageFormat.PNG.matched(mimeType) && (regionSize == imageSize && VERSION.SDK_INT >= VERSION_CODES.N)) {
//        width = ceil(widthValue).toInt()
//        height = ceil(heightValue).toInt()
//    } else {
//        width = floor(widthValue).toInt()
//        height = floor(heightValue).toInt()
//    }
//    return Size(width, height)
}

/**
 * Read image information using BitmapFactory. Ignore the Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfoWithIgnoreExifOrientation
 */
@Throws(IOException::class)
actual fun DataSource.readImageInfoWithIgnoreExifOrientation(): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, this@apply)
        }
    }
    val mimeType = boundOptions.outMimeType.orEmpty()
    val imageSize = Size(width = boundOptions.outWidth, height = boundOptions.outHeight)
    return ImageInfo(size = imageSize, mimeType = mimeType)
        .apply { checkImageInfo(this) }
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
 * Decode Android Drawable width, height, type information
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfo
 */
internal fun Drawable.readImageInfo(mimeType: String? = null): ImageInfo {
    val imageSize = Size(intrinsicWidth, intrinsicHeight)
    return ImageInfo(size = imageSize, mimeType = mimeType ?: "image/png")
        .apply { checkImageInfo(this) }
}

/**
 * Decode bitmap using BitmapFactory. Parse Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testDecode
 */
@Throws(IOException::class, ImageInvalidException::class)
fun DataSource.decode(
    config: DecodeConfig? = null,
    exifOrientationHelper: ExifOrientationHelper? = null
): Bitmap = openSource().buffer().inputStream().use {
    val options = config?.toBitmapOptions()
    val bitmap = BitmapFactory.decodeStream(it, null, options)
        ?: throw ImageInvalidException("decode return null")
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
@Throws(IOException::class, ImageInvalidException::class)
fun DataSource.decodeRegion(
    srcRect: Rect,
    config: DecodeConfig? = null,
    imageSize: Size? = null,
    exifOrientationHelper: ExifOrientationHelper? = null
): Bitmap = openSource().buffer().inputStream().use {
    @Suppress("DEPRECATION")
    val regionDecoder = if (VERSION.SDK_INT >= VERSION_CODES.S) {
        BitmapRegionDecoder.newInstance(it)
    } else {
        BitmapRegionDecoder.newInstance(it, false)
    } ?: throw IOException("BitmapRegionDecoder.newInstance return null")
    val imageSize1 = imageSize ?: readImageInfo(exifOrientationHelper).size
    val exifOrientationHelper1 =
        exifOrientationHelper ?: ExifOrientationHelper(readExifOrientation())
    val originalRegion = exifOrientationHelper1.applyToRect(
        srcRect = srcRect,
        spaceSize = imageSize1,
        reverse = true
    )
    val bitmapOptions = config?.toBitmapOptions()
    val regionBitmap = try {
        regionDecoder.decodeRegion(originalRegion.toAndroidRect(), bitmapOptions)
            ?: throw ImageInvalidException("decode return null")
    } finally {
        regionDecoder.recycle()
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
fun supportBitmapRegionDecoder(mimeType: String, animated: Boolean = false): Boolean {
    require(mimeType.startsWith("image/"))
    return when (mimeType) {
        ImageFormat.JPEG.mimeType -> true
        ImageFormat.PNG.mimeType -> true
        ImageFormat.WEBP.mimeType -> !animated || VERSION.SDK_INT >= VERSION_CODES.O
        ImageFormat.GIF.mimeType -> false
        ImageFormat.BMP.mimeType -> false
        ImageFormat.SVG.mimeType -> false
        ImageFormat.HEIC.mimeType -> VERSION.SDK_INT >= VERSION_CODES.O_MR1
        ImageFormat.HEIF.mimeType -> VERSION.SDK_INT >= VERSION_CODES.O_MR1
        // For the AVIF format, BitmapFactory starts to support it from API 31.
        // But BitmapRegionDecoder still does not support it until API 35.
        // At present, it can only be assumed that API 36 starts to support it.
        ImageFormat.AVIF.mimeType -> VERSION.SDK_INT > 35
        // Other formats are supported by default, In order to prevent BitmapRegionDecoder from supporting new formats in the future, our failure to adapt will result in unavailability.
        else -> true    // TODO null
    }
}