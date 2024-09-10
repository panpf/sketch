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

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.SkiaImageInfo
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.toSkiaRect
import okio.buffer
import okio.use
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import kotlin.math.ceil


/* ************************************** sampling ********************************************** */

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


/* **************************************** decode ********************************************* */

/**
 * Decode the image by sampling
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testDecode
 */
internal fun SkiaImage.decode(decodeConfig: DecodeConfig? = null): SkiaBitmap {
    val sampleSize = decodeConfig?.inSampleSize ?: 1
    val bitmapSize = calculateSampledBitmapSize(
        imageSize = Size(width, height),
        sampleSize = sampleSize
    )
    val newColorType = decodeConfig?.colorType ?: colorType
    val newImageInfo = SkiaImageInfo(
        width = bitmapSize.width,
        height = bitmapSize.height,
        colorType = newColorType,
        alphaType = alphaType,
        colorSpace = colorSpace
    )
    val bitmap = SkiaBitmap(newImageInfo)
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
internal fun SkiaImage.decodeRegion(
    srcRect: SketchRect,
    decodeConfig: DecodeConfig? = null
): SkiaBitmap {
    val sampleSize = decodeConfig?.inSampleSize ?: 1
    val bitmapSize = calculateSampledBitmapSize(
        imageSize = Size(srcRect.width(), srcRect.height()),
        sampleSize = sampleSize
    )
    val newColorType = decodeConfig?.colorType ?: colorType
    val newImageInfo = SkiaImageInfo(
        width = bitmapSize.width,
        height = bitmapSize.height,
        colorType = newColorType,
        alphaType = alphaType,
        colorSpace = colorSpace
    )
    val bitmap = SkiaBitmap(newImageInfo)
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = srcRect.toSkiaRect(),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}

/**
 * Decode image width, height, MIME type and other information
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfo
 */
fun DataSource.readImageInfo(): ImageInfo {
    val bytes = openSource().buffer().use { it.readByteArray() }
    val imageSize = SkiaImage.makeFromEncoded(bytes).use {
        Size(it.width, it.height)
    }
    val mimeType = Codec.makeFromData(Data.makeFromBytes(bytes)).use {
        "image/${it.encodedImageFormat.name.lowercase()}"
    }
    return ImageInfo(size = imageSize, mimeType = mimeType)
}