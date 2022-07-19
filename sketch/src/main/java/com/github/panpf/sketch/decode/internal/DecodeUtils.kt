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
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIC
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIF
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toHexString
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

val maxBitmapSize: Size by lazy {
    OpenGLTextureHelper.maxSize?.let {
        Size(it, it)
    } ?: Canvas().let {
        Size(it.maximumBitmapWidth, it.maximumBitmapHeight)
    }
}

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL
 */
fun limitedMaxBitmapSize(@Px imageWidth: Int, @Px imageHeight: Int, inSampleSize: Int): Int {
    val maximumBitmapSize = maxBitmapSize
    var finalInSampleSize = inSampleSize.coerceAtLeast(1)
    while ((samplingSize(imageWidth, finalInSampleSize) > maximumBitmapSize.width)
        || (samplingSize(imageHeight, finalInSampleSize) > maximumBitmapSize.height)
    ) {
        finalInSampleSize *= 2
    }
    return finalInSampleSize
}

/**
 * Calculate the sample size for [BitmapFactory.Options]
 */
fun calculateSampleSize(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    @Px targetWidth: Int,
    @Px targetHeight: Int,
): Int {
    val targetPixels = targetWidth * targetHeight
    var sampleSize = 1
    while (
        samplingSize(imageWidth, sampleSize) * samplingSize(imageHeight, sampleSize) > targetPixels
    ) {
        sampleSize *= 2
    }
    return limitedMaxBitmapSize(imageWidth, imageHeight, sampleSize)
}


fun samplingSize(size: Int, sampleSize: Double): Int {
    return ceil(size / sampleSize).toInt()
}

fun samplingSize(size: Int, sampleSize: Int): Int {
    return samplingSize(size, sampleSize.toDouble())
}

fun samplingSizeForRegion(size: Int, sampleSize: Double): Int {
    val value = size / sampleSize
    return if (VERSION.SDK_INT >= VERSION_CODES.N) ceil(value).toInt() else floor(value).toInt()
}

fun samplingSizeForRegion(size: Int, sampleSize: Int): Int {
    return samplingSizeForRegion(size, sampleSize.toDouble())
}


fun Size.sampling(sampleSize: Double): Size {
    return Size(samplingSize(width, sampleSize), samplingSize(height, sampleSize))
}

fun Size.sampling(sampleSize: Int): Size {
    return sampling(sampleSize.toDouble())
}

fun Size.samplingForRegion(sampleSize: Double): Size {
    return Size(samplingSizeForRegion(width, sampleSize), samplingSizeForRegion(height, sampleSize))
}

fun Size.samplingForRegion(sampleSize: Int): Size {
    return samplingForRegion(sampleSize.toDouble())
}


fun Size.samplingByTarget(@Px targetWidth: Int, @Px targetHeight: Int): Size {
    val sampleSize = calculateSampleSize(width, height, targetWidth, targetHeight)
    return sampling(sampleSize)
}

fun Size.samplingByTarget(targetSize: Size): Size {
    val sampleSize = calculateSampleSize(width, height, targetSize.width, targetSize.height)
    return sampling(sampleSize)
}


fun Size.samplingForRegionByTarget(@Px targetWidth: Int, @Px targetHeight: Int): Size {
    val sampleSize = calculateSampleSize(width, height, targetWidth, targetHeight)
    return samplingForRegion(sampleSize)
}

fun Size.samplingForRegionByTarget(targetSize: Size): Size {
    val sampleSize = calculateSampleSize(width, height, targetSize.width, targetSize.height)
    return samplingForRegion(sampleSize)
}

fun computeSizeMultiplier(
    @Px srcWidth: Int,
    @Px srcHeight: Int,
    @Px dstWidth: Int,
    @Px dstHeight: Int,
    fitScale: Boolean
): Double {
    val widthPercent = dstWidth / srcWidth.toDouble()
    val heightPercent = dstHeight / srcHeight.toDouble()
    return if (fitScale) {
        min(widthPercent, heightPercent)
    } else {
        max(widthPercent, heightPercent)
    }
}

fun realDecode(
    request: ImageRequest,
    dataFrom: DataFrom,
    imageInfo: ImageInfo,
    exifOrientation: Int,
    decodeFull: (decodeConfig: DecodeConfig) -> Bitmap,
    decodeRegion: ((srcRect: Rect, decodeConfig: DecodeConfig) -> Bitmap)?
): BitmapDecodeResult {
    val exifOrientationHelper = ExifOrientationHelper(
        if (!request.ignoreExifOrientation) {
            exifOrientation
        } else {
            ExifInterface.ORIENTATION_UNDEFINED
        }
    )

    val resize = request.resize
    val applySize = exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
    val addedResize = resize?.let { exifOrientationHelper.addToResize(it, applySize) }
    val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
    val resizeTransformed: String?
    val bitmap = if (addedResize?.shouldClip(imageInfo.width, imageInfo.height) == true) {
        val precision = addedResize.getPrecision(imageInfo.width, imageInfo.height)
        val scale = addedResize.getScale(imageInfo.width, imageInfo.height)
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            precision = precision,
            resizeScale = scale,
        )
        // In cases where clipping is required, the clipping region is used to calculate inSampleSize, this will give you a clearer picture
        decodeConfig.inSampleSize = calculateSampleSize(
            resizeMapping.srcRect.width(),
            resizeMapping.srcRect.height(),
            resizeMapping.destRect.width(),
            resizeMapping.destRect.height(),
        )
        if (precision != LESS_PIXELS && decodeRegion != null) {
            resizeTransformed = createResizeTransformed(resize)
            decodeRegion(resizeMapping.srcRect, decodeConfig)
        } else {
            resizeTransformed = null
            decodeFull(decodeConfig)
        }
    } else {
        resizeTransformed = null
        decodeConfig.inSampleSize = addedResize?.let {
            calculateSampleSize(imageInfo.width, imageInfo.height, it.width, it.height)
        } ?: limitedMaxBitmapSize(imageInfo.width, imageInfo.height, 1)
        decodeFull(decodeConfig)
    }

    return BitmapDecodeResult.Builder(bitmap, imageInfo, exifOrientation, dataFrom).apply {
        decodeConfig.inSampleSize?.takeIf { it > 1 && bitmap.width < imageInfo.width }?.let {
            addTransformed(createInSampledTransformed(it))
        }
        resizeTransformed?.let {
            addTransformed(it)
        }
    }.build()
}

fun BitmapDecodeResult.applyExifOrientation(
    bitmapPool: BitmapPool? = null,
    ignoreExifOrientation: Boolean = false,
): BitmapDecodeResult {
    if (ignoreExifOrientation
        || imageExifOrientation == ExifInterface.ORIENTATION_UNDEFINED
        || imageExifOrientation == ExifInterface.ORIENTATION_NORMAL
    ) {
        return this
    }
    val exifOrientationHelper = ExifOrientationHelper(imageExifOrientation)
    val inBitmap = bitmap
    val newBitmap = exifOrientationHelper.applyToBitmap(inBitmap, bitmapPool) ?: return this
    bitmapPool?.free(inBitmap, "applyExifOrientation")
    return newResult(newBitmap) {
        addTransformed(createExifOrientationTransformed(imageExifOrientation))
        val newSize = exifOrientationHelper.applyToSize(
            Size(imageInfo.width, imageInfo.height)
        )
        imageInfo(ImageInfo(newSize.width, newSize.height, imageInfo.mimeType))
    }
}

fun BitmapDecodeResult.applyResize(
    sketch: Sketch,
    resize: Resize?,
): BitmapDecodeResult {
    if (resize == null) return this
    val inBitmap = bitmap
    val precision = resize.getPrecision(inBitmap.width, inBitmap.height)
    val newBitmap = if (precision == LESS_PIXELS) {
        val sampleSize = calculateSampleSize(
            inBitmap.width, inBitmap.height, resize.width, resize.height
        )
        if (sampleSize != 1) {
            inBitmap.scaled(1 / sampleSize.toDouble(), sketch.bitmapPool)
        } else {
            null
        }
    } else if (resize.shouldClip(inBitmap.width, inBitmap.height)) {
        val scale = resize.getScale(inBitmap.width, inBitmap.height)
        val mapping = calculateResizeMapping(
            imageWidth = inBitmap.width,
            imageHeight = inBitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            precision = precision,
            resizeScale = scale,
        )
        val config = inBitmap.safeConfig
        sketch.bitmapPool.getOrCreate(mapping.newWidth, mapping.newHeight, config).apply {
            Canvas(this).drawBitmap(inBitmap, mapping.srcRect, mapping.destRect, null)
        }
    } else {
        null
    }
    return if (newBitmap != null) {
        sketch.bitmapPool.free(inBitmap, "applyResize")
        newResult(newBitmap) {
            addTransformed(createResizeTransformed(resize))
        }
    } else {
        this
    }
}

@Throws(IOException::class)
fun DataSource.readImageInfoWithBitmapFactory(): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    decodeBitmap(boundOptions)
    return ImageInfo(
        width = boundOptions.outWidth,
        height = boundOptions.outHeight,
        mimeType = boundOptions.outMimeType.orEmpty()
    )
}

@Throws(IOException::class)
fun DataSource.readImageInfoWithBitmapFactoryOrThrow(): ImageInfo {
    val imageInfo = readImageInfoWithBitmapFactory()
    val width = imageInfo.width
    val height = imageInfo.height
    if (width <= 0 || height <= 0) {
        throw Exception("Invalid image, size=${width}x${height}")
    }
    return imageInfo
}

fun DataSource.readImageInfoWithBitmapFactoryOrNull(): ImageInfo? =
    try {
        readImageInfoWithBitmapFactory().takeIf {
            it.width > 0 && it.height > 0
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }


@Throws(IOException::class)
fun DataSource.decodeBitmap(options: BitmapFactory.Options? = null): Bitmap? =
    newInputStream().buffered().use {
        BitmapFactory.decodeStream(it, null, options)
    }

fun ImageFormat.supportBitmapRegionDecoder(): Boolean =
    this == ImageFormat.JPEG
            || this == ImageFormat.PNG
            || this == ImageFormat.WEBP
            || (VERSION.SDK_INT >= VERSION_CODES.P && (this == HEIF || this == HEIC))

@Throws(IOException::class)
fun DataSource.decodeRegionBitmap(srcRect: Rect, options: BitmapFactory.Options? = null): Bitmap? =
    newInputStream().buffered().use {
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

val Bitmap.logString: String
    get() = "${width}x${height}/${config}@${toHexString()}"

val Bitmap.sizeString: String
    get() = "${width}x${height}"

fun ImageRequest.newDecodeConfigByQualityParams(mimeType: String): DecodeConfig =
    DecodeConfig().apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed) {
            inPreferQualityOverSpeed = true
        }

        val newConfig = bitmapConfig?.getConfig(mimeType)
        if (newConfig != null) {
            inPreferredConfig = newConfig
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != null) {
            inPreferredColorSpace = colorSpace
        }
    }