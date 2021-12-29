package com.github.panpf.sketch.common.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DecodeException
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ImageType
import com.github.panpf.sketch.common.ListenerInfo
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.common.decode.DecodeResult
import com.github.panpf.sketch.common.decode.Decoder
import com.github.panpf.sketch.load.ImageInfo
import com.github.panpf.sketch.load.Resize

class BitmapFactoryDecoder(
    private val sketch: Sketch,
    private val request: LoadableRequest,
    private val dataSource: DataSource,
) : Decoder {

    private val thumbnailModeDecodeHelper = ThumbnailModeDecodeHelper()
    private val normalDecodeHelper = NormalDecodeHelper()

    companion object {
        const val MODULE = "BitmapFactoryDecoder"
    }

    override suspend fun decode(): DecodeResult {
        val imageInfo = readImageInfo()
        val resize = request.resize
        val imageType = ImageType.valueOfMimeType(imageInfo.mimeType)
        val bitmapPoolHelper = sketch.bitmapPoolHelper

        val imageOrientationCorrector =
            ImageOrientationCorrector.fromExifOrientation(imageInfo.exifOrientation)

        val decodeOptions = BitmapFactory.Options().apply {
            if (request.inPreferQualityOverSpeed == true) {
                inPreferQualityOverSpeed = true
            }

            val newConfig = request.bitmapConfig?.getConfigByMimeType(imageInfo.mimeType)
            if (newConfig != null) {
                inPreferredConfig = newConfig
            }
        }
        val bitmap =
            if (thumbnailModeDecodeHelper.canUseThumbnailMode(resize, imageInfo, imageType)) {
                thumbnailModeDecodeHelper
                    .decode(
                        sketch,
                        request,
                        dataSource,
                        imageInfo,
                        decodeOptions,
                        imageOrientationCorrector
                    )
            } else {
                normalDecodeHelper.decode(
                    sketch,
                    request,
                    dataSource,
                    imageInfo,
                    decodeOptions,
                    imageOrientationCorrector
                )
            }

        val correctedOrientationBitmap =
            imageOrientationCorrector?.rotateBitmap(bitmap, bitmapPoolHelper.bitmapPool) ?: bitmap
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
        val boundOptions = BitmapFactory.Options()
        boundOptions.inJustDecodeBounds = true
        dataSource.decodeBitmap(boundOptions)
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            val message = "Invalid image size. size=%dx%d, uri=%s"
                .format(boundOptions.outWidth, boundOptions.outHeight, request.uri)
            SLog.em(MODULE, message)
            throw DecodeException(message)
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

    private fun tryResize(bitmap: Bitmap, resize: Resize?): Bitmap {
        return when (resize?.sizeMode) {
            Resize.SizeMode.ASPECT_RATIO_SAME -> {
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
            Resize.SizeMode.EXACTLY_SAME -> {
                if (resize.width != bitmap.width || resize.height != bitmap.height) {
                    resize(bitmap, resize)
                } else {
                    bitmap
                }
            }
            else -> {
                bitmap
            }
        }
    }

    // todo Try resize and rotateBitmap together
    private fun resize(bitmap: Bitmap, resize: Resize): Bitmap {
        val mapping = ResizeCalculator().calculator(
            imageWidth = bitmap.width,
            imageHeight = bitmap.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            scaleType = resize.scaleType,
            exactlySame = resize.sizeMode == Resize.SizeMode.EXACTLY_SAME
        )
        val config = bitmap.config ?: ARGB_8888
        val bitmapPool = sketch.bitmapPoolHelper.bitmapPool
        val resizeBitmap = bitmapPool.getOrMake(mapping.imageWidth, mapping.imageHeight, config)
        val canvas = Canvas(resizeBitmap)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null)
        return resizeBitmap
    }

    class Factory : Decoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            listenerInfo: ListenerInfo<ImageRequest, ImageResult>?,
            dataSource: DataSource,
        ): Decoder? = if (request is LoadableRequest) {
            BitmapFactoryDecoder(sketch, request, dataSource)
        } else {
            null
        }
    }
}