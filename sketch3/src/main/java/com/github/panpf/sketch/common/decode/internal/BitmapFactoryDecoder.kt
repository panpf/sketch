package com.github.panpf.sketch.common.decode.internal

import android.graphics.BitmapFactory
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

class BitmapFactoryDecoder(
    private val sketch: Sketch,
    private val request: LoadableRequest,
    private val listenerInfo: ListenerInfo<ImageRequest, ImageResult>?,
    private val dataSource: DataSource,
) : Decoder {

    private val thumbnailModeDecodeHelper = ThumbnailModeDecodeHelper()
    private val normalDecodeHelper = NormalDecodeHelper()

    companion object {
        const val MODULE = "BitmapFactoryDecoder"
    }

    override suspend fun decode(): DecodeResult {
        val imageInfo = readImageInfo()
        if (imageInfo.width <= 1 || imageInfo.height <= 1) {
            val message = "Invalid image size. size=%dx%d, uri=%s".format(imageInfo.width, imageInfo.height, request.uri)
            SLog.em(MODULE, message)
            throw DecodeException(message)
        }
        val imageType = ImageType.valueOfMimeType(imageInfo.mimeType)

        val resize = request.resize

        val decodeOptions = BitmapFactory.Options().apply {
            if (request.inPreferQualityOverSpeed == true) {
                inPreferQualityOverSpeed = true
            }

            val newConfig = request.bitmapConfig?.getConfigByMimeType(imageInfo.mimeType)
            if (newConfig != null) {
                inPreferredConfig = newConfig
            }
        }
        return if (thumbnailModeDecodeHelper.canUseThumbnailMode(resize, imageInfo, imageType)) {
            thumbnailModeDecodeHelper.decode(sketch, request, dataSource, imageInfo, decodeOptions)
        } else {
            normalDecodeHelper.decode(sketch, request, dataSource, imageInfo, decodeOptions)
        }
    }

    private fun readImageInfo(): ImageInfo {
        val boundOptions = BitmapFactory.Options()
        boundOptions.inJustDecodeBounds = true
        dataSource.decodeBitmap(boundOptions)
        val exifOrientation: Int =
            ImageOrientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource)
        return ImageInfo(
            boundOptions.outMimeType,
            boundOptions.outWidth,
            boundOptions.outHeight,
            exifOrientation
        )
    }

    class Factory : Decoder.Factory {
        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            listenerInfo: ListenerInfo<ImageRequest, ImageResult>?,
            dataSource: DataSource,
        ): Decoder? = if (request is LoadableRequest) {
            BitmapFactoryDecoder(sketch, request, listenerInfo, dataSource)
        } else {
            null
        }
    }
}