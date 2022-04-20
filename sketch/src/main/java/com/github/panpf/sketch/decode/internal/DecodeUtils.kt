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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.ImageFormat.HEIC
import com.github.panpf.sketch.ImageFormat.HEIF
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toHexString
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
    while ((calculateSamplingSize(imageWidth, finalInSampleSize) > maximumBitmapSize.width)
        || (calculateSamplingSize(imageHeight, finalInSampleSize) > maximumBitmapSize.height)
    ) {
        finalInSampleSize *= 2
    }
    return finalInSampleSize
}

/**
 * Calculate the sample size for [BitmapFactory.Options]
 */
private fun realCalculateSampleSize(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    @Px targetWidth: Int,
    @Px targetHeight: Int,
    targetPixelsScale: Float = 1f
): Int {
    val targetPixels = targetWidth.times(targetHeight).times(targetPixelsScale).roundToInt()
    var sampleSize = 1
    while (
        calculateSamplingSize(imageWidth, sampleSize)
            .times(calculateSamplingSize(imageHeight, sampleSize)) > targetPixels
    ) {
        sampleSize *= 2
    }
    return limitedMaxBitmapSize(imageWidth, imageHeight, sampleSize)
}

/**
 * Calculate the sample size for [BitmapFactory.Options]
 */
fun calculateSampleSize(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    @Px targetWidth: Int,
    @Px targetHeight: Int,
): Int = realCalculateSampleSize(imageWidth, imageHeight, targetWidth, targetHeight, 1f)

/**
 * Calculate the sample size for [BitmapFactory.Options]. 10% tolerance
 */
fun calculateSampleSizeWithTolerance(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    @Px targetWidth: Int,
    @Px targetHeight: Int,
): Int = realCalculateSampleSize(imageWidth, imageHeight, targetWidth, targetHeight, 1.1f)

fun calculateSamplingSize(size: Int, sampleSize: Int): Int {
    return ceil((size / sampleSize.toDouble())).toInt()
}

fun calculateSamplingSizeForRegion(size: Int, sampleSize: Int): Int {
    return floor((size / sampleSize.toDouble())).toInt()
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
    val resizeTransformed: ResizeTransformed?
    val bitmap = if (addedResize?.shouldClip(request.context, imageInfo.width, imageInfo.height) == true) {
        val precision = addedResize.getPrecision(request.context, imageInfo.width, imageInfo.height)
        val scale = addedResize.getScale(request.context, imageInfo.width, imageInfo.height)
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            precision = precision,
            resizeScale = scale,
        )
        // In cases where clipping is required, the clipping region is used to calculate inSampleSize, this will give you a clearer picture
        decodeConfig.inSampleSize = calculateSampleSizeWithTolerance(
            resizeMapping.srcRect.width(),
            resizeMapping.srcRect.height(),
            resizeMapping.destRect.width(),
            resizeMapping.destRect.height(),
        )
        if (decodeRegion != null) {
            resizeTransformed = ResizeTransformed(resize)
            decodeRegion(resizeMapping.srcRect, decodeConfig)
        } else {
            resizeTransformed = null
            decodeFull(decodeConfig)
        }
    } else {
        resizeTransformed = null
        decodeConfig.inSampleSize = addedResize?.let {
            calculateSampleSizeWithTolerance(imageInfo.width, imageInfo.height, it.width, it.height)
        } ?: limitedMaxBitmapSize(imageInfo.width, imageInfo.height, 1)
        decodeFull(decodeConfig)
    }

    return BitmapDecodeResult.Builder(bitmap, imageInfo, exifOrientation, dataFrom).apply {
        decodeConfig.inSampleSize?.takeIf { it > 1 }?.let {
            addTransformed(InSampledTransformed(it))
        }
        resizeTransformed?.let {
            addTransformed(it)
        }
    }.build()
}

fun BitmapDecodeResult.applyExifOrientation(
    bitmapPool: BitmapPool,
    ignoreExifOrientation: Boolean,
): BitmapDecodeResult {
    val exifOrientationHelper = if (!ignoreExifOrientation) {
        ExifOrientationHelper(exifOrientation)
    } else {
        ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
    }
    val inBitmap = bitmap
    val newBitmap = exifOrientationHelper.applyToBitmap(inBitmap, bitmapPool)
    return if (newBitmap != null) {
        bitmapPool.free(inBitmap)
        newResult(newBitmap) {
            addTransformed(ExifOrientationTransformed(exifOrientationHelper.exifOrientation))
            val newSize = exifOrientationHelper.applyToSize(
                Size(imageInfo.width, imageInfo.height)
            )
            imageInfo(ImageInfo(newSize.width, newSize.height, imageInfo.mimeType))
        }
    } else {
        this
    }
}

fun BitmapDecodeResult.applyResize(
    context: Context,
    bitmapPool: BitmapPool,
    resize: Resize?,
): BitmapDecodeResult {
    val inBitmap = bitmap
    return if (resize?.shouldClip(context, inBitmap.width, inBitmap.height) == true) {
        val precision = resize.getPrecision(context, inBitmap.width, inBitmap.height)
        val scale = resize.getScale(context, inBitmap.width, inBitmap.height)
        val mapping = calculateResizeMapping(
            imageWidth = inBitmap.width,
            imageHeight = inBitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            precision = precision,
            resizeScale = scale,
        )
        val config = inBitmap.config ?: ARGB_8888
        val newBitmap = bitmapPool.getOrCreate(mapping.newWidth, mapping.newHeight, config)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(inBitmap, mapping.srcRect, mapping.destRect, null)
        bitmapPool.free(inBitmap)
        newResult(newBitmap) {
            addTransformed(ResizeTransformed(resize))
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
    decodeBitmapWithBitmapFactory(boundOptions)
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
    val mimeType = imageInfo.mimeType
    if (width <= 0 || height <= 0 || mimeType.isEmpty()) {
        throw Exception("Invalid image, size=${width}x${height}, imageType='${mimeType}'")
    }
    return imageInfo
}

fun DataSource.readImageInfoWithBitmapFactoryOrNull(): ImageInfo? =
    try {
        readImageInfoWithBitmapFactory().takeIf {
            it.width > 0 && it.height > 0 && it.mimeType.isNotEmpty()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
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

val Bitmap.logString: String
    get() = "${width}x${height}/${config}@${toHexString()}"

val Bitmap.sizeString: String
    get() = "${width}x${height}"

fun ImageRequest.newDecodeConfigByQualityParams(mimeType: String): DecodeConfig =
    DecodeConfig().apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed) {
            inPreferQualityOverSpeed = true
        }

        val newConfig = bitmapConfig?.getConfigByMimeType(mimeType)
        if (newConfig != null) {
            inPreferredConfig = newConfig
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != null) {
            inPreferredColorSpace = colorSpace
        }
    }