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
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toHexString
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL
 */
fun limitedOpenGLTextureMaxSize(@Px imageWidth: Int, @Px imageHeight: Int, inSampleSize: Int): Int {
    val openGLTextureMaxSize = OpenGLTextureHelper.maxSize ?: return inSampleSize
    var finalInSampleSize = inSampleSize.coerceAtLeast(1)
    while ((calculateSamplingSize(imageWidth, finalInSampleSize) > openGLTextureMaxSize)
        || (calculateSamplingSize(imageHeight, finalInSampleSize) > openGLTextureMaxSize)
    ) {
        finalInSampleSize *= 2
    }
    return finalInSampleSize
}

/**
 * Calculate the inSampleSize for [BitmapFactory.Options]
 */
fun calculateInSampleSize(
    @Px imageWidth: Int,
    @Px imageHeight: Int,
    @Px targetWidth: Int,
    @Px targetHeight: Int,
): Int {
    val newTargetWidth: Int = targetWidth
    val newTargetHeight: Int = targetHeight

    val targetScale = 1.1f
    val targetPixels = newTargetWidth.times(newTargetHeight).times(targetScale).roundToInt()
    var inSampleSize = 1
    while (true) {
        val sampledWidth = calculateSamplingSize(imageWidth, inSampleSize)
        val sampledHeight = calculateSamplingSize(imageHeight, inSampleSize)
        if (sampledWidth.times(sampledHeight) <= targetPixels) {
            break
        } else {
            inSampleSize *= 2
        }
    }
    return limitedOpenGLTextureMaxSize(imageWidth, imageHeight, inSampleSize)
}

fun calculateSamplingSize(value1: Int, inSampleSize: Int): Int {
    return ceil((value1 / inSampleSize.toFloat()).toDouble()).toInt()
}

fun calculateSamplingSizeForRegion(value1: Int, inSampleSize: Int): Int {
    return floor((value1 / inSampleSize.toFloat()).toDouble()).toInt()
}

fun realDecode(
    request: LoadRequest,
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
    val bitmap = if (addedResize?.shouldClip(imageInfo.width, imageInfo.height) == true) {
        val precision = addedResize.precision(imageInfo.width, imageInfo.height)
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            resizeScale = addedResize.scale,
            exactlySize = precision == EXACTLY
        )
        // In cases where clipping is required, the clipping region is used to calculate inSampleSize, this will give you a clearer picture
        decodeConfig.inSampleSize = calculateInSampleSize(
            resizeMapping.srcRect.width(),
            resizeMapping.srcRect.height(),
            addedResize.width,
            addedResize.height
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
            calculateInSampleSize(imageInfo.width, imageInfo.height, it.width, it.height)
        } ?: 1
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
        new(newBitmap) {
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
    bitmapPool: BitmapPool,
    resize: Resize?,
): BitmapDecodeResult {
    val inBitmap = bitmap
    return if (resize?.shouldClip(inBitmap.width, inBitmap.height) == true) {
        val precision = resize.precision(inBitmap.width, inBitmap.height)
        val mapping = calculateResizeMapping(
            imageWidth = inBitmap.width,
            imageHeight = inBitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            resizeScale = resize.scale,
            exactlySize = precision == EXACTLY
        )
        val config = inBitmap.config ?: ARGB_8888
        val newBitmap = bitmapPool.getOrCreate(mapping.newWidth, mapping.newHeight, config)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(inBitmap, mapping.srcRect, mapping.destRect, null)
        bitmapPool.free(inBitmap)
        new(newBitmap) {
            addTransformed(ResizeTransformed(resize))
        }
    } else {
        this
    }
}

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
    readImageInfoWithBitmapFactory().takeIf {
        it.width > 0 && it.height > 0 && it.mimeType.isNotEmpty()
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