/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat.BMP
import com.github.panpf.sketch.decode.internal.ImageFormat.GIF
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIC
import com.github.panpf.sketch.decode.internal.ImageFormat.HEIF
import com.github.panpf.sketch.decode.internal.ImageFormat.JPEG
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.decode.internal.ImageFormat.WEBP
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.configOrNull
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toAndroidRect
import com.github.panpf.sketch.util.toHexString
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
 */
fun calculateSampledBitmapSize(
    imageSize: Size, sampleSize: Int, mimeType: String? = null
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
 */
fun calculateSampledBitmapSizeForRegion(
    regionSize: Size, sampleSize: Int, mimeType: String? = null, imageSize: Size? = null
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

private fun checkSampledSize(
    sampledSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean
): Boolean {
    return if (smallerSizeMode) {
        sampledSize.width <= targetSize.width && sampledSize.height <= targetSize.height
    } else {
        sampledSize.width * sampledSize.height <= targetSize.width * targetSize.height
    }
}

/**
 * Calculate the sample size, support for BitmapFactory or ImageDecoder
 */
fun calculateSampleSize(
    imageSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String? = null
): Int {
    var sampleSize = 1
    var accepted = false
    while (!accepted) {
        val sampledBitmapSize = calculateSampledBitmapSize(imageSize, sampleSize, mimeType)
        accepted = checkSampledSize(
            sampledSize = sampledBitmapSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode
        )
        if (!accepted) {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSize(
        sampleSize = sampleSize,
        imageSize = imageSize,
        targetSize = targetSize,
        mimeType = mimeType
    )
}

/**
 * Calculate the sample size, support for BitmapFactory or ImageDecoder
 */
fun calculateSampleSize(
    imageSize: Size,
    targetSize: Size,
    mimeType: String? = null
): Int = calculateSampleSize(
    imageSize = imageSize,
    targetSize = targetSize,
    smallerSizeMode = false,
    mimeType = mimeType
)

/**
 * Calculate the sample size, support for BitmapRegionDecoder
 */
fun calculateSampleSizeForRegion(
    regionSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String? = null,
    imageSize: Size? = null
): Int {
    var sampleSize = 1
    var accepted = false
    while (!accepted) {
        val bitmapSize = calculateSampledBitmapSizeForRegion(
            regionSize, sampleSize, mimeType, imageSize
        )
        accepted = checkSampledSize(
            sampledSize = bitmapSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode
        )
        if (!accepted) {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSizeForRegion(
        sampleSize = sampleSize,
        regionSize = regionSize,
        targetSize = targetSize,
        mimeType = mimeType,
        imageSize = imageSize
    )
}

/**
 * Calculate the sample size, support for BitmapRegionDecoder
 */
fun calculateSampleSizeForRegion(
    regionSize: Size,
    targetSize: Size,
    mimeType: String? = null,
    imageSize: Size? = null
): Int = calculateSampleSizeForRegion(
    regionSize = regionSize,
    targetSize = targetSize,
    smallerSizeMode = false,
    mimeType = mimeType,
    imageSize = imageSize
)


/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL, support for BitmapFactory or ImageDecoder
 */
fun limitedSampleSizeByMaxBitmapSize(
    sampleSize: Int, imageSize: Size, targetSize: Size, mimeType: String? = null
): Int {
    val maxBitmapSize = OpenGLTextureHelper.maxSize
        ?.let { Size(it, it) }
        ?: Size(targetSize.width * 2, targetSize.height * 2)
    var finalSampleSize = sampleSize.coerceAtLeast(1)
    while (true) {
        val bitmapSize = calculateSampledBitmapSize(imageSize, finalSampleSize, mimeType)
        if (bitmapSize.width <= maxBitmapSize.width && bitmapSize.height <= maxBitmapSize.height) {
            break
        } else {
            finalSampleSize *= 2
        }
    }
    return finalSampleSize
}

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL, support for BitmapRegionDecoder
 */
fun limitedSampleSizeByMaxBitmapSizeForRegion(
    sampleSize: Int,
    regionSize: Size,
    targetSize: Size,
    mimeType: String? = null,
    imageSize: Size? = null
): Int {
    val maximumBitmapSize = OpenGLTextureHelper.maxSize
        ?.let { Size(it, it) }
        ?: Size(targetSize.width * 2, targetSize.height * 2)
    var finalSampleSize = sampleSize.coerceAtLeast(1)
    while (true) {
        val bitmapSize = calculateSampledBitmapSizeForRegion(
            regionSize, finalSampleSize, mimeType, imageSize
        )
        if (bitmapSize.width <= maximumBitmapSize.width && bitmapSize.height <= maximumBitmapSize.height) {
            break
        } else {
            finalSampleSize *= 2
        }
    }
    return finalSampleSize
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

@WorkerThread
fun realDecode(
    requestContext: RequestContext,
    dataFrom: DataFrom,
    imageInfo: ImageInfo,
    decodeFull: (decodeConfig: DecodeConfig) -> Bitmap,
    decodeRegion: ((srcRect: Rect, decodeConfig: DecodeConfig) -> Bitmap)?
): DecodeResult {
    requiredWorkThread()
    val request = requestContext.request
    val size = requestContext.size!!
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
    val imageSize = Size(imageInfo.width, imageInfo.height)
    val appliedImageSize = exifOrientationHelper.applyToSize(imageSize)
    val resize = Resize(
        width = size.width,
        height = size.height,
        precision = request.precisionDecider.get(
            imageSize = appliedImageSize,
            targetSize = size,
        ),
        scale = request.scaleDecider.get(
            imageSize = appliedImageSize,
            targetSize = size,
        )
    )
    val addedResize = exifOrientationHelper.addToResize(resize, appliedImageSize)
    val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
    val transformedList = mutableListOf<String>()
    val bitmap = if (
        addedResize.shouldClip(imageInfo.width, imageInfo.height)
        && addedResize.precision != LESS_PIXELS
        && decodeRegion != null
    ) {
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            precision = addedResize.precision,
            scale = addedResize.scale,
        )
        val sampleSize = calculateSampleSizeForRegion(
            regionSize = Size(resizeMapping.srcRect.width(), resizeMapping.srcRect.height()),
            targetSize = Size(resizeMapping.destRect.width(), resizeMapping.destRect.height()),
            smallerSizeMode = addedResize.precision.isSmallerSizeMode(),
            mimeType = imageInfo.mimeType,
            imageSize = imageSize
        )
        if (sampleSize > 1) {
            transformedList.add(createInSampledTransformed(sampleSize))
        }
        transformedList.add(createSubsamplingTransformed(resizeMapping.srcRect.toAndroidRect()))
        decodeConfig.inSampleSize = sampleSize
        decodeRegion(resizeMapping.srcRect.toAndroidRect(), decodeConfig)
    } else {
        val sampleSize = run {
            val targetSize = Size(addedResize.width, addedResize.height)
            calculateSampleSize(
                imageSize = imageSize,
                targetSize = targetSize,
                smallerSizeMode = addedResize.precision.isSmallerSizeMode(),
                mimeType = imageInfo.mimeType
            )
        }
        if (sampleSize > 1) {
            transformedList.add(0, createInSampledTransformed(sampleSize))
        }
        decodeConfig.inSampleSize = sampleSize
        decodeFull(decodeConfig)
    }
    return DecodeResult(
        image = bitmap.asSketchImage(resources = requestContext.request.context.resources),
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        transformedList = transformedList.takeIf { it.isNotEmpty() }?.toList(),
        extras = null,
    )
}

@WorkerThread
fun DecodeResult.appliedExifOrientation(
    sketch: Sketch,
    requestContext: RequestContext
): DecodeResult {
    requiredWorkThread()
    if (transformedList?.getExifOrientationTransformed() != null
        || imageInfo.exifOrientation == ExifInterface.ORIENTATION_UNDEFINED
        || imageInfo.exifOrientation == ExifInterface.ORIENTATION_NORMAL
    ) {
        return this
    }
    if (image !is BitmapImage) return this
    val request = requestContext.request
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
    val inputBitmap = image.bitmap
    val newBitmap = exifOrientationHelper.applyToBitmap(
        inBitmap = inputBitmap,
//        bitmapPool = sketch.bitmapPool,
        disallowReuseBitmap = request.disallowReuseBitmap
    ) ?: return this
//    sketch.bitmapPool.freeBitmap(
//        bitmap = inputBitmap,
//        disallowReuseBitmap = request.disallowReuseBitmap,
//        caller = "appliedExifOrientation"
//    )
    sketch.logger.d("appliedExifOrientation") {
        "appliedExifOrientation. freeBitmap. bitmap=${inputBitmap.logString}. '${requestContext.logKey}'"
    }

    val newSize = exifOrientationHelper.applyToSize(
        Size(imageInfo.width, imageInfo.height)
    )
    sketch.logger.d("appliedExifOrientation") {
        "appliedExifOrientation. successful. ${newBitmap.logString}. ${imageInfo}. '${requestContext.logKey}'"
    }
    return newResult(
        image = newBitmap.asSketchImage(resources = image.resources),
        imageInfo = imageInfo.newImageInfo(width = newSize.width, height = newSize.height)
    ) {
        addTransformed(createExifOrientationTransformed(imageInfo.exifOrientation))
    }
}

@WorkerThread
fun DecodeResult.appliedResize(
    sketch: Sketch,
    requestContext: RequestContext,
): DecodeResult {
    requiredWorkThread()
    if (image !is BitmapImage) return this
    val request = requestContext.request
    val size = requestContext.size!!
    val resize = Resize(
        width = size.width,
        height = size.height,
        precision = request.precisionDecider.get(
            imageSize = Size(imageInfo.width, imageInfo.height),
            targetSize = size,
        ),
        scale = request.scaleDecider.get(
            imageSize = Size(imageInfo.width, imageInfo.height),
            targetSize = size,
        )
    )
    val inputBitmap = image.bitmap
    val newBitmap = if (resize.precision == LESS_PIXELS) {
        val sampleSize = calculateSampleSize(
            imageSize = Size(inputBitmap.width, inputBitmap.height),
            targetSize = Size(resize.width, resize.height),
            smallerSizeMode = resize.precision.isSmallerSizeMode()
        )
        if (sampleSize != 1) {
            inputBitmap.scaled(
                scale = 1 / sampleSize.toDouble(),
//                bitmapPool = sketch.bitmapPool,
                disallowReuseBitmap = request.disallowReuseBitmap
            )
        } else {
            null
        }
    } else if (resize.shouldClip(inputBitmap.width, inputBitmap.height)) {
        val mapping = calculateResizeMapping(
            imageWidth = inputBitmap.width,
            imageHeight = inputBitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            precision = resize.precision,
            scale = resize.scale,
        )
        val config = inputBitmap.safeConfig
        // TODO BitmapPool
//        sketch.bitmapPool.getOrCreate(
//            width = mapping.newWidth,
//            height = mapping.newHeight,
//            config = config,
//            disallowReuseBitmap = request.disallowReuseBitmap,
//            caller = "appliedResize"
//        )
        Bitmap.createBitmap(
            /* width = */ mapping.newWidth,
            /* height = */ mapping.newHeight,
            /* config = */ config,
        ).apply {
            Canvas(this).drawBitmap(
                inputBitmap,
                mapping.srcRect.toAndroidRect(),
                mapping.destRect.toAndroidRect(),
                null
            )
        }
    } else {
        null
    }
    return if (newBitmap != null) {
        sketch.logger.d("appliedResize") {
            "appliedResize. successful. ${newBitmap.logString}. ${imageInfo}. '${requestContext.logKey}'"
        }
//        sketch.bitmapPool.freeBitmap(
//            bitmap = inputBitmap,
//            disallowReuseBitmap = request.disallowReuseBitmap,
//            caller = "appliedResize"
//        )
        sketch.logger.d("appliedResize") {
            "appliedResize. freeBitmap. bitmap=${inputBitmap.logString}. '${requestContext.logKey}'"
        }
        newResult(image = newBitmap.asSketchImage(resources = image.resources)) {
            addTransformed(createResizeTransformed(resize))
        }
    } else {
        this
    }
}


@Throws(IOException::class)
fun BasedStreamDataSource.readImageInfoWithBitmapFactory(ignoreExifOrientation: Boolean = false): ImageInfo {
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

@Throws(IOException::class, ImageInvalidException::class)
fun BasedStreamDataSource.readImageInfoWithBitmapFactoryOrThrow(ignoreExifOrientation: Boolean = false): ImageInfo {
    val imageInfo = readImageInfoWithBitmapFactory(ignoreExifOrientation)
    val width = imageInfo.width
    val height = imageInfo.height
    if (width <= 0 || height <= 0) {
        throw ImageInvalidException("Invalid image. size=${width}x${height}")
    }
    return imageInfo
}

@WorkerThread
fun BasedStreamDataSource.readImageInfoWithBitmapFactoryOrNull(ignoreExifOrientation: Boolean = false): ImageInfo? =
    try {
        readImageInfoWithBitmapFactory(ignoreExifOrientation).takeIf {
            it.width > 0 && it.height > 0
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }


@Throws(IOException::class)
fun BasedStreamDataSource.decodeBitmap(options: BitmapFactory.Options? = null): Bitmap? =
    newInputStream().buffered().use {
        BitmapFactory.decodeStream(it, null, options)
    }

@SuppressLint("ObsoleteSdkInt")
fun ImageFormat.supportBitmapRegionDecoder(): Boolean =
    this == JPEG
            || this == PNG
            || this == WEBP
            || (this == HEIC && VERSION.SDK_INT >= VERSION_CODES.P)
            || (this == HEIF && VERSION.SDK_INT >= VERSION_CODES.P)

@Throws(IOException::class)
fun BasedStreamDataSource.decodeRegionBitmap(
    srcRect: Rect,
    options: BitmapFactory.Options? = null
): Bitmap? =
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
    get() = "Bitmap(${width}x${height},$configOrNull,@${toHexString()})"

val Bitmap.sizeString: String
    get() = "${width}x${height}"

fun ImageRequest.newDecodeConfigByQualityParams(mimeType: String): DecodeConfig =
    DecodeConfig().apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed) {
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

/**
 * If true, indicates that the given mimeType and sampleSize combination can be using 'inBitmap' in BitmapFactory
 *
 * Test results based on the BitmapFactoryTest.testInBitmapAndInSampleSize() method
 */
@SuppressLint("ObsoleteSdkInt")
fun isSupportInBitmap(mimeType: String?, sampleSize: Int): Boolean =
    when (ImageFormat.parseMimeType(mimeType)) {
        JPEG -> if (sampleSize == 1) VERSION.SDK_INT >= 16 else VERSION.SDK_INT >= 19
        PNG -> if (sampleSize == 1) VERSION.SDK_INT >= 16 else VERSION.SDK_INT >= 19
        GIF -> if (sampleSize == 1) VERSION.SDK_INT >= 19 else VERSION.SDK_INT >= 21
        WEBP -> VERSION.SDK_INT >= 19
//        ImageFormat.WEBP -> VERSION.SDK_INT >= 26 animated
        BMP -> VERSION.SDK_INT >= 19
        HEIC -> false
        HEIF -> VERSION.SDK_INT >= 28
        else -> VERSION.SDK_INT >= 32   // Compatible with new image types supported in the future
    }

/**
 * If true, indicates that the given mimeType can be using 'inBitmap' in BitmapRegionDecoder
 *
 * Test results based on the BitmapRegionDecoderTest.testInBitmapAndInSampleSize() method
 */
@SuppressLint("ObsoleteSdkInt")
fun isSupportInBitmapForRegion(mimeType: String?): Boolean =
    when (ImageFormat.parseMimeType(mimeType)) {
        JPEG -> VERSION.SDK_INT >= 16
        PNG -> VERSION.SDK_INT >= 16
        GIF -> false
        WEBP -> VERSION.SDK_INT >= 16
//        ImageFormat.WEBP -> VERSION.SDK_INT >= 26 animated
        BMP -> false
        HEIC -> VERSION.SDK_INT >= 28
        HEIF -> VERSION.SDK_INT >= 28
        else -> VERSION.SDK_INT >= 32   // Compatible with new image types supported in the future
    }

fun Precision.isSmallerSizeMode(): Boolean {
    return this == Precision.SMALLER_SIZE
}