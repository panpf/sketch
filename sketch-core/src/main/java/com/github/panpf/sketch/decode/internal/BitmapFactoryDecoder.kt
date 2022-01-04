package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory.Options
import android.graphics.Canvas
import android.graphics.Point
import com.github.panpf.sketch.ImageType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.request.DecodeException
import com.github.panpf.sketch.request.ImageInfo
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.Resize
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.newDecodeOptionsByQualityParams
import com.github.panpf.sketch.util.calculateInSampleSize
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.supportBitmapRegionDecoder

class BitmapFactoryDecoder(
    private val sketch: Sketch,
    private val request: LoadRequest,
    private val dataSource: DataSource,
) : Decoder {

    private val bitmapPoolHelper = sketch.bitmapPoolHelper
    private val logger = sketch.logger

    companion object {
        const val MODULE = "BitmapFactoryDecoder"
    }

    override suspend fun decode(): DecodeResult {
        val imageInfo = readImageInfo()

        val resize = request.resize
        val imageType = ImageType.valueOfMimeType(imageInfo.mimeType)
        val decodeOptions = request.newDecodeOptionsByQualityParams(imageInfo.mimeType)
        val imageOrientationCorrector =
            ImageOrientationCorrector.fromExifOrientation(imageInfo.exifOrientation)

        val bitmap = if (resize != null && shouldUseRegionDecoder(resize, imageInfo, imageType)) {
            decodeUseRegion(resize, imageInfo, decodeOptions, imageOrientationCorrector)
        } else {
            decodeUseBitmapFactory(imageInfo, decodeOptions, imageOrientationCorrector)
        }

        val correctedOrientationBitmap =
            imageOrientationCorrector?.rotateBitmap(bitmap, bitmapPoolHelper) ?: bitmap
        if (correctedOrientationBitmap !== bitmap) {
            bitmapPoolHelper.freeBitmapToPool(bitmap)
        }

        val resizeBitmap = tryResize(correctedOrientationBitmap, resize)
        if (resizeBitmap !== correctedOrientationBitmap) {
            bitmapPoolHelper.freeBitmapToPool(correctedOrientationBitmap)
        }

        return DecodeResult(resizeBitmap, imageInfo, decodeOptions)
    }

    private fun readImageInfo(): ImageInfo {
        val boundOptions = Options()
        boundOptions.inJustDecodeBounds = true
        dataSource.decodeBitmap(boundOptions)
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            throw DecodeException(
                "Invalid image size. size=%dx%d, uri=%s"
                    .format(boundOptions.outWidth, boundOptions.outHeight, request.uriString)
            )
        }

        val exifOrientation: Int =
            ImageOrientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource)
        return ImageInfo(
            boundOptions.outMimeType,
            boundOptions.outWidth,
            boundOptions.outHeight,
            exifOrientation
        )
    }

    private fun shouldUseRegionDecoder(
        resize: Resize, imageInfo: ImageInfo, imageType: ImageType?
    ): Boolean {
        if (imageType?.supportBitmapRegionDecoder() == true) {
            val resizeAspectRatio = (resize.width.toFloat() / resize.height.toFloat()).format(1)
            val imageAspectRatio =
                (imageInfo.width.toFloat() / imageInfo.height.toFloat()).format(1)
            return if (resize.mode == Resize.Mode.THUMBNAIL_MODE) {
                val maxAspectRatio = resizeAspectRatio.coerceAtLeast(imageAspectRatio)
                val minAspectRatio = resizeAspectRatio.coerceAtMost(imageAspectRatio)
                maxAspectRatio > minAspectRatio * resize.minAspectRatio
            } else {
                resizeAspectRatio != imageAspectRatio
            }
        }
        return false
    }

    private fun decodeUseRegion(
        resize: Resize,
        imageInfo: ImageInfo,
        decodeOptions: Options,
        imageOrientationCorrector: ImageOrientationCorrector?
    ): Bitmap {
        val imageSize = Point(imageInfo.width, imageInfo.height)

//        if (Build.VERSION.SDK_INT <= VERSION_CODES.M && !decodeOptions.inPreferQualityOverSpeed) {
//            decodeOptions.inPreferQualityOverSpeed = true
//        }

        imageOrientationCorrector?.rotateSize(imageSize)

        val resizeMapping = ResizeMapping.calculator(
            imageWidth = imageSize.x,
            imageHeight = imageSize.y,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            scaleType = resize.scaleType,
            exactlySame = false
        )
        val resizeMappingSrcWidth = resizeMapping.srcRect.width()
        val resizeMappingSrcHeight = resizeMapping.srcRect.height()

        val resizeInSampleSize = calculateInSampleSize(
            resizeMappingSrcWidth, resizeMappingSrcHeight, resize.width, resize.height
        )
        decodeOptions.inSampleSize = resizeInSampleSize

        imageOrientationCorrector
            ?.reverseRotateRect(resizeMapping.srcRect, imageSize.x, imageSize.y)

        if (request.disabledBitmapPool != true) {
            bitmapPoolHelper.setInBitmapForRegionDecoder(
                decodeOptions, resizeMappingSrcWidth, resizeMappingSrcHeight
            )
        }

        val bitmap = try {
            dataSource.decodeRegionBitmap(resizeMapping.srcRect, decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            when {
                inBitmap != null && isInBitmapError(throwable, true) -> {
                    val message = "Bitmap region decode error. Because inBitmap. uri=%s"
                        .format(request.uriString)
                    logger.e(MODULE, throwable, message)

                    decodeOptions.inBitmap = null
                    bitmapPoolHelper.freeBitmapToPool(inBitmap)
                    try {
                        dataSource.decodeRegionBitmap(resizeMapping.srcRect, decodeOptions)
                    } catch (throwable2: Throwable) {
                        throw DecodeException(
                            "Bitmap region decode error. uri=%s".format(request.uriString),
                            throwable2
                        )
                    }
                }
                isSrcRectError(throwable, imageSize.x, imageSize.y, resizeMapping.srcRect) -> {
                    throw DecodeException(
                        "Bitmap region decode error. Because srcRect. imageInfo=%s, resize=%s, srcRect=%s, uri=%s"
                            .format(
                                imageInfo, request.resize, resizeMapping.srcRect, request.uriString
                            ),
                        throwable
                    )
                }
                else -> {
                    throw DecodeException(
                        "Bitmap region decode error. uri=%s".format(request.uriString),
                        throwable
                    )
                }
            }
        }
            ?: throw DecodeException("Bitmap region decode return null. uri=%s".format(request.uriString))
        if (bitmap.width <= 1 || bitmap.height <= 1) {
            bitmap.recycle()
            throw DecodeException(
                "Invalid image size. size=%dx%d, uri=%s"
                    .format(bitmap.width, bitmap.height, request.uriString)
            )
        }
        return bitmap
    }

    private fun decodeUseBitmapFactory(
        imageInfo: ImageInfo,
        decodeOptions: Options,
        imageOrientationCorrector: ImageOrientationCorrector?
    ): Bitmap {
        val imageSize = Point(imageInfo.width, imageInfo.height)
        imageOrientationCorrector?.rotateSize(imageSize)

        val maxSizeInSampleSize = request.maxSize?.let {
            calculateInSampleSize(imageSize.x, imageSize.y, it.width, it.height)
        } ?: 1
        val resizeInSampleSize = request.resize?.let {
            calculateInSampleSize(imageSize.x, imageSize.y, it.width, it.height)
        } ?: 1
        decodeOptions.inSampleSize = maxSizeInSampleSize.coerceAtLeast(resizeInSampleSize)

        // Set inBitmap from bitmap pool
        if (request.disabledBitmapPool != true) {
            bitmapPoolHelper.setInBitmap(
                decodeOptions, imageSize.x, imageSize.y, imageInfo.mimeType
            )
        }

        val bitmap: Bitmap = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable, false)) {
                val message = "Bitmap decode error. Because inBitmap. uri=%s"
                    .format(request.uriString)
                logger.e(MODULE, throwable, message)

                decodeOptions.inBitmap = null
                bitmapPoolHelper.freeBitmapToPool(inBitmap)
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    throw DecodeException(
                        "Bitmap decode error. uri=%s".format(request.uriString),
                        throwable2
                    )
                }
            } else {
                throw DecodeException(
                    "Bitmap decode error. uri=%s".format(request.uriString),
                    throwable
                )
            }
        } ?: throw DecodeException("Bitmap decode return null. uri=%s".format(request.uriString))
        if (bitmap.width <= 1 || bitmap.height <= 1) {
            bitmap.recycle()
            throw DecodeException(
                "Invalid image size. size=%dx%d, uri=%s"
                    .format(bitmap.width, bitmap.height, request.uriString)
            )
        }
        return bitmap
    }

    private fun tryResize(bitmap: Bitmap, resize: Resize?): Bitmap = when (resize?.mode) {
        Resize.Mode.EXACTLY_SAME -> {
            if (resize.width != bitmap.width || resize.height != bitmap.height) {
                resize(bitmap, resize)
            } else {
                bitmap
            }
        }
        Resize.Mode.ASPECT_RATIO_SAME -> {
            val resizeAspectRatio =
                "%.1f".format((resize.width.toFloat() / resize.height.toFloat()))
            val bitmapAspectRatio =
                "%.1f".format((bitmap.width.toFloat() / bitmap.height.toFloat()))
            if (resizeAspectRatio != bitmapAspectRatio) {
                resize(bitmap, resize)
            } else {
                bitmap
            }
        }
        Resize.Mode.THUMBNAIL_MODE -> {
            bitmap
        }
        else -> {
            bitmap
        }
    }

    // todo Try resize and rotateBitmap together
    private fun resize(bitmap: Bitmap, resize: Resize): Bitmap {
        val mapping = ResizeMapping.calculator(
            imageWidth = bitmap.width,
            imageHeight = bitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            scaleType = resize.scaleType,
            exactlySame = resize.mode == Resize.Mode.EXACTLY_SAME
        )
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val resizeBitmap =
            sketch.bitmapPoolHelper.getOrMake(mapping.newWidth, mapping.newHeight, config)
        val canvas = Canvas(resizeBitmap)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null)
        return resizeBitmap
    }

    class Factory : Decoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            dataSource: DataSource,
        ): Decoder? = if (request is LoadRequest) {
            BitmapFactoryDecoder(sketch, request, dataSource)
        } else {
            null
        }
    }
}