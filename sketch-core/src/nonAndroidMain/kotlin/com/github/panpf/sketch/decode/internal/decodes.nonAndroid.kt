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
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.toSkiaRect
import okio.buffer
import okio.use
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import kotlin.math.ceil


/* ************************************** sampling ********************************************** */

actual fun getMaxBitmapSize(targetSize: Size): Size {
    return Size(targetSize.width * 2, targetSize.height * 2)
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
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
 * Calculate the size of the sampled Bitmap, support for BitmapRegionDecoder
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


internal fun Image.decode(sampleSize: Int): SkiaBitmap {
    val bitmapSize = calculateSampledBitmapSize(Size(width, height), sampleSize)
    val bitmap = Bitmap().apply {
        allocN32Pixels(bitmapSize.width, bitmapSize.height)
    }
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = Rect.makeWH(width.toFloat(), height.toFloat()),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}

internal fun Image.decodeRegion(srcRect: SketchRect, sampleSize: Int): SkiaBitmap {
    val bitmapSize = calculateSampledBitmapSize(Size(srcRect.width(), srcRect.height()), sampleSize)
    val bitmap = Bitmap().apply {
        allocN32Pixels(bitmapSize.width, bitmapSize.height)
    }
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = srcRect.toSkiaRect(),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}

fun DataSource.readImageInfo(): ImageInfo {
    val bytes = openSource().buffer().use { it.readByteArray() }
    val image = Image.makeFromEncoded(bytes)
    val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
    val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
    return ImageInfo(
        width = image.width,
        height = image.height,
        mimeType = mimeType,
    )
}