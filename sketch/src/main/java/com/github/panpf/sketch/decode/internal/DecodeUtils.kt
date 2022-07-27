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


// todo apply
fun calculateBitmapSizeBySampleSizeForBitmapFactory(
    imageSize: Size, sampleSize: Int, mimeType: String? = null
): Size {
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val isPNGFormat = ImageFormat.PNG.matched(mimeType)
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

// todo apply
fun calculateBitmapSizeBySampleSizeForBitmapRegionDecoder(
    regionSize: Size, sampleSize: Int, imageSize: Size? = null
): Size {
    val widthValue = regionSize.width / sampleSize.toDouble()
    val heightValue = regionSize.height / sampleSize.toDouble()
    val width: Int
    val height: Int
    if (VERSION.SDK_INT >= VERSION_CODES.N && regionSize == imageSize) {
        width = ceil(widthValue).toInt()
        height = ceil(heightValue).toInt()
    } else {
        width = floor(widthValue).toInt()
        height = floor(heightValue).toInt()
    }
    return Size(width, height)
}


/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL
 */
fun limitedSampleSizeByMaxBitmapSize(imageSize: Size, sampleSize: Int): Int {
    val maximumBitmapSize = maxBitmapSize
    var finalInSampleSize = sampleSize.coerceAtLeast(1)
    while (true) {
        val sampledWidth = ceil(imageSize.width / finalInSampleSize.toDouble()).toInt()
        val sampledHeight = ceil(imageSize.height / finalInSampleSize.toDouble()).toInt()
        if (sampledWidth <= maximumBitmapSize.width && sampledHeight <= maximumBitmapSize.height) {
            break
        } else {
            finalInSampleSize *= 2
        }
    }
    return finalInSampleSize
}

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL for BitmapFactory
 */
fun limitedSampleSizeByMaxBitmapSizeForBitmapFactory(
    imageSize: Size, sampleSize: Int, mimeType: String? = null
): Int {
    val maximumBitmapSize = maxBitmapSize
    var finalSampleSize = sampleSize.coerceAtLeast(1)
    while (true) {
        val bitmapSize =
            calculateBitmapSizeBySampleSizeForBitmapFactory(imageSize, finalSampleSize, mimeType)
        if (bitmapSize.width <= maximumBitmapSize.width && bitmapSize.height <= maximumBitmapSize.height) {
            break
        } else {
            finalSampleSize *= 2
        }
    }
    return finalSampleSize
}

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL for BitmapRegionDecoder
 */
fun limitedSampleSizeByMaxBitmapSizeForBitmapRegionDecoder(
    regionSize: Size, sampleSize: Int, imageSize: Size? = null
): Int {
    val maximumBitmapSize = maxBitmapSize
    var finalSampleSize = sampleSize.coerceAtLeast(1)
    while (true) {
        val bitmapSize =
            calculateBitmapSizeBySampleSizeForBitmapRegionDecoder(
                regionSize,
                finalSampleSize,
                imageSize
            )
        if (bitmapSize.width <= maximumBitmapSize.width && bitmapSize.height <= maximumBitmapSize.height) {
            break
        } else {
            finalSampleSize *= 2
        }
    }
    return finalSampleSize
}

/**
 * Calculate the sample size
 */
fun calculateSampleSize(imageSize: Size, targetSize: Size): Int {
    val targetPixels = targetSize.width * targetSize.height
    var sampleSize = 1
    while (true) {
        val sampledWidth = ceil(imageSize.width / sampleSize.toDouble()).toInt()
        val sampledHeight = ceil(imageSize.height / sampleSize.toDouble()).toInt()
        if (sampledWidth * sampledHeight <= targetPixels) {
            break
        } else {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSize(imageSize, sampleSize)
}

/**
 * Calculate the sample size for BitmapFactory
 */
fun calculateSampleSizeForBitmapFactory(
    imageSize: Size, targetSize: Size, mimeType: String? = null
): Int {
    val targetPixels = targetSize.width * targetSize.height
    var sampleSize = 1
    while (true) {
        val bitmapSize =
            calculateBitmapSizeBySampleSizeForBitmapFactory(imageSize, sampleSize, mimeType)
        if (bitmapSize.width * bitmapSize.height <= targetPixels) {
            break
        } else {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSizeForBitmapFactory(imageSize, sampleSize, mimeType)
}

/**
 * Calculate the sample size for BitmapRegionDecoder
 */
fun calculateSampleSizeForBitmapRegionDecoder(
    regionSize: Size, targetSize: Size, imageSize: Size? = null
): Int {
    val targetPixels = targetSize.width * targetSize.height
    var sampleSize = 1
    while (true) {
        val bitmapSize =
            calculateBitmapSizeBySampleSizeForBitmapRegionDecoder(regionSize, sampleSize, imageSize)
        if (bitmapSize.width * bitmapSize.height <= targetPixels) {
            break
        } else {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSizeForBitmapRegionDecoder(regionSize, sampleSize, imageSize)
}

fun samplingSize(size: Int, sampleSize: Double, mimeType: String? = null): Int {
    return if (mimeType != null && ImageFormat.PNG.matched(mimeType)) {
        floor(size / sampleSize).toInt()
    } else {
        ceil(size / sampleSize).toInt()
    }
}

fun samplingSize(size: Int, sampleSize: Int, mimeType: String? = null): Int {
    return samplingSize(size, sampleSize.toDouble(), mimeType)
}

fun samplingSizeForRegion(size: Int, sampleSize: Double): Int {
    // 当 size 和图片的宽或高一样时，N 以上版本应该用 ceil，但此种情况不应该使用 BitmapRegionDecoder
    return floor(size / sampleSize).toInt()
}

fun samplingSizeForRegion(size: Int, sampleSize: Int): Int {
    return samplingSizeForRegion(size, sampleSize.toDouble())
}


fun Size.sampling(sampleSize: Double, mimeType: String? = null): Size {
    return Size(
        samplingSize(width, sampleSize, mimeType),
        samplingSize(height, sampleSize, mimeType)
    )
}

fun Size.sampling(sampleSize: Int, mimeType: String? = null): Size {
    return sampling(sampleSize.toDouble(), mimeType)
}

fun Size.samplingForRegion(sampleSize: Double): Size {
    return Size(samplingSizeForRegion(width, sampleSize), samplingSizeForRegion(height, sampleSize))
}

fun Size.samplingForRegion(sampleSize: Int): Size {
    return samplingForRegion(sampleSize.toDouble())
}


fun Size.samplingByTarget(
    @Px targetWidth: Int,
    @Px targetHeight: Int,
    mimeType: String? = null
): Size {
    val sampleSize = calculateSampleSize(Size(width, height), Size(targetWidth, targetHeight))
    return sampling(sampleSize, mimeType)
}

fun Size.samplingByTarget(targetSize: Size, mimeType: String? = null): Size {
    val sampleSize = calculateSampleSize(Size(width, height), targetSize)
    return sampling(sampleSize, mimeType)
}


fun Size.samplingForRegionByTarget(@Px targetWidth: Int, @Px targetHeight: Int): Size {
    val sampleSize = calculateSampleSize(Size(width, height), Size(targetWidth, targetHeight))
    return samplingForRegion(sampleSize)
}

fun Size.samplingForRegionByTarget(targetSize: Size): Size {
    val sampleSize = calculateSampleSize(Size(width, height), targetSize)
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
    decodeFull: (decodeConfig: DecodeConfig) -> Bitmap,
    decodeRegion: ((srcRect: Rect, decodeConfig: DecodeConfig) -> Bitmap)?
): BitmapDecodeResult {
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
    val resize = request.resize
    val appliedImageSize =
        exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
    val addedResize = resize?.let { exifOrientationHelper.addToResize(it, appliedImageSize) }
    val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
    val transformeds = mutableListOf<String>()
    val precision = if (addedResize?.shouldClip(imageInfo.width, imageInfo.height) == true) {
        addedResize.getPrecision(imageInfo.width, imageInfo.height)
    } else {
        null
    }
    val bitmap = if (
        addedResize != null
        && precision != null
        && precision != LESS_PIXELS
        && decodeRegion != null
    ) {
        val scale = addedResize.getScale(imageInfo.width, imageInfo.height)
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            precision = precision,
            resizeScale = scale,
        )
        decodeConfig.inSampleSize = calculateSampleSizeForBitmapRegionDecoder(
            regionSize = Size(resizeMapping.srcRect.width(), resizeMapping.srcRect.height()),
            targetSize = Size(resizeMapping.destRect.width(), resizeMapping.destRect.height()),
            imageSize = Size(imageInfo.width, imageInfo.height)
        ).apply {
            if (this > 1) {
                transformeds.add(createInSampledTransformed(this))
            }
        }
        transformeds.add(createResizeTransformed(resize))
        decodeRegion(resizeMapping.srcRect, decodeConfig)
    } else {
        decodeConfig.inSampleSize = (addedResize?.let {
            calculateSampleSizeForBitmapFactory(
                imageSize = Size(imageInfo.width, imageInfo.height),
                targetSize = Size(it.width, it.height),
                mimeType = imageInfo.mimeType
            )
        } ?: limitedSampleSizeByMaxBitmapSizeForBitmapFactory(
            imageSize = Size(imageInfo.width, imageInfo.height),
            sampleSize = 1,
            mimeType = imageInfo.mimeType
        )).apply {
            if (this > 1) {
                transformeds.add(createInSampledTransformed(this))
            }
        }
        decodeFull(decodeConfig)
    }
    return BitmapDecodeResult(
        bitmap = bitmap,
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        transformedList = transformeds.takeIf { it.isNotEmpty() }
    )
}

fun BitmapDecodeResult.applyExifOrientation(bitmapPool: BitmapPool? = null): BitmapDecodeResult {
    if (imageInfo.exifOrientation == ExifInterface.ORIENTATION_UNDEFINED
        || imageInfo.exifOrientation == ExifInterface.ORIENTATION_NORMAL
    ) {
        return this
    }
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
    val inBitmap = bitmap
    val newBitmap = exifOrientationHelper.applyToBitmap(inBitmap, bitmapPool) ?: return this
    bitmapPool?.free(inBitmap, "applyExifOrientation")
    val newSize = exifOrientationHelper.applyToSize(
        Size(imageInfo.width, imageInfo.height)
    )
    return newResult(
        bitmap = newBitmap,
        imageInfo = imageInfo.newImageInfo(width = newSize.width, height = newSize.height)
    ) {
        addTransformed(createExifOrientationTransformed(imageInfo.exifOrientation))
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
            imageSize = Size(inBitmap.width, inBitmap.height),
            targetSize = Size(resize.width, resize.height)
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
        newResult(bitmap = newBitmap) {
            addTransformed(createResizeTransformed(resize))
        }
    } else {
        this
    }
}

@Throws(IOException::class)
fun DataSource.readImageInfoWithBitmapFactory(ignoreExifOrientation: Boolean = false): ImageInfo {
    val boundOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    decodeBitmap(boundOptions)
    val mimeType = boundOptions.outMimeType.orEmpty()
    val exifOrientation = if (!ignoreExifOrientation) {
        readExifOrientationWithMimeType(mimeType)
    } else {
        ExifInterface.ORIENTATION_UNDEFINED
    }
    return ImageInfo(
        width = boundOptions.outWidth,
        height = boundOptions.outHeight,
        mimeType = mimeType,
        exifOrientation = exifOrientation,
    )
}

@Throws(IOException::class)
fun DataSource.readImageInfoWithBitmapFactoryOrThrow(ignoreExifOrientation: Boolean = false): ImageInfo {
    val imageInfo = readImageInfoWithBitmapFactory(ignoreExifOrientation)
    val width = imageInfo.width
    val height = imageInfo.height
    if (width <= 0 || height <= 0) {
        throw Exception("Invalid image, size=${width}x${height}")
    }
    return imageInfo
}

fun DataSource.readImageInfoWithBitmapFactoryOrNull(ignoreExifOrientation: Boolean = false): ImageInfo? =
    try {
        readImageInfoWithBitmapFactory(ignoreExifOrientation).takeIf {
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