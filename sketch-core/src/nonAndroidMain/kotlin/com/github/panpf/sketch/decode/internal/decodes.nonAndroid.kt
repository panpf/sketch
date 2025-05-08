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

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.core.BuildKonfig
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.compareVersions
import com.github.panpf.sketch.util.toSkiaRect
import okio.buffer
import okio.use
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import kotlin.math.ceil

/**
 * Get the maximum Bitmap size allowed by the Skia platform
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testGetMaxBitmapSize
 */
actual fun getMaxBitmapSize(): Size? = null

/**
 * Calculate the size of the sampled Bitmap, support for Skia Image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampledBitmapSize
 */
actual fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String?
): Size {
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val width: Int = ceil(widthValue).toInt()
    val height: Int = ceil(heightValue).toInt()
    return Size(width, height)
}

/**
 * Calculate the size of the sampled Bitmap, support for Skia Image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampledBitmapSizeForRegion
 */
actual fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String?,
    imageSize: Size?
): Size = calculateSampledBitmapSize(
    imageSize = regionSize,
    sampleSize = sampleSize,
    mimeType = mimeType
)


/**
 * Decode image width, height, MIME type and other information. Ignore the Exif orientation
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfoWithIgnoreExifOrientation
 */
fun readImageInfoWithIgnoreExifOrientation(codec: Codec): ImageInfo {
    val imageSize = Size(width = codec.width, height = codec.height)
    val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
    return ImageInfo(size = imageSize, mimeType = mimeType)
        .apply { checkImageInfo(this) }
}

/**
 * Decode image width, height, MIME type and other information. Ignore the Exif orientation
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfoWithIgnoreExifOrientation
 */
actual fun DataSource.readImageInfoWithIgnoreExifOrientation(): ImageInfo {
    val bytes = openSource().buffer().use { it.readByteArray() }
    return Codec.makeFromData(Data.makeFromBytes(bytes)).use {
        readImageInfoWithIgnoreExifOrientation(it)
    }
}

/**
 * Decode image width, height, MIME type and other information. Should be able to parse the exif orientation
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfo
 */
fun readImageInfo(codec: Codec, skiaImage: Image): ImageInfo {
    val imageSize = Size(width = skiaImage.width, height = skiaImage.height)
    val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
    return ImageInfo(size = imageSize, mimeType = mimeType)
        .apply { checkImageInfo(this) }
}

/**
 * Decode image width, height, MIME type and other information. Should be able to parse the exif orientation
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfo
 */
actual fun DataSource.readImageInfo(): ImageInfo {
    val bytes = openSource().buffer().use { it.readByteArray() }
    return Codec.makeFromData(Data.makeFromBytes(bytes)).use { codec ->
        Image.makeFromEncoded(bytes).use { skiaImage ->
            readImageInfo(codec, skiaImage)
        }
    }
}


/**
 * Decode the image by sampling
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testDecode
 */
internal fun Image.decode(config: DecodeConfig? = null): Bitmap {
    val sampleSize = config?.sampleSize ?: 1
    val bitmapSize = calculateSampledBitmapSize(
        imageSize = Size(width, height),
        sampleSize = sampleSize
    )
    val newColorType = config?.colorType ?: colorType
    val newColorSpace = config?.colorSpace ?: colorSpace
    val newImageInfo = org.jetbrains.skia.ImageInfo(
        width = bitmapSize.width,
        height = bitmapSize.height,
        colorType = newColorType,
        alphaType = alphaType,
        colorSpace = newColorSpace
    )
    val bitmap = createBitmap(newImageInfo)
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = Rect.makeWH(width.toFloat(), height.toFloat()),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}

/**
 * Decode the specified region of the image by sampling
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testDecodeRegion
 */
internal fun Image.decodeRegion(
    srcRect: SketchRect,
    config: DecodeConfig? = null
): Bitmap {
    if (srcRect.isEmpty) {
        throw IllegalArgumentException("srcRect is empty. $srcRect")
    }
    val sampleSize = config?.sampleSize ?: 1
    val bitmapSize = calculateSampledBitmapSize(
        imageSize = Size(srcRect.width(), srcRect.height()),
        sampleSize = sampleSize
    )
    val newColorType = config?.colorType ?: colorType
    val newColorSpace = config?.colorSpace ?: colorSpace
    val newImageInfo = org.jetbrains.skia.ImageInfo(
        width = bitmapSize.width,
        height = bitmapSize.height,
        colorType = newColorType,
        alphaType = alphaType,
        colorSpace = newColorSpace
    )
    val bitmap = createBitmap(newImageInfo)
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = srcRect.toSkiaRect(),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}

/**
 * Check whether the Skia platform supports decoding the specified region
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testSupportDecodeRegion
 */
fun supportDecodeRegion(mimeType: String): Boolean? {
    if (!mimeType.startsWith("image/")) {
        return false
    }
    return when (mimeType) {
        "image/jpeg", "image/png", "image/webp", "image/bmp", "image/gif" -> true
        "image/svg+xml" -> false
        "image/heic", "image/heif", "image/avif" ->
            if (compareVersions(BuildKonfig.SKIKO_VERSION_NAME, "0.9.4") <= 0) false else null

        else -> null
    }
}