package com.github.panpf.sketch.common.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DecodeException
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ImageType
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.common.decode.DecodeResult
import com.github.panpf.sketch.common.decode.Decoder
import com.github.panpf.sketch.load.ImageInfo
import com.github.panpf.sketch.load.Resize
import com.github.panpf.sketch.util.supportBitmapRegionDecoder
import java.io.IOException

class BitmapFactoryDecoder(
    private val sketch: Sketch,
    private val request: LoadableRequest,
    private val extras: RequestExtras<ImageRequest, ImageResult>?,
    private val source: DataSource,
) : Decoder {

    private val imageOrientationCorrector = ImageOrientationCorrector()
    private val thumbnailModeDecodeHelper = ThumbnailModeDecodeHelper(imageOrientationCorrector)

    companion object {
        const val MODULE = "BitmapFactoryDecoder"
    }

    override suspend fun decode(): DecodeResult {
        val imageInfo = readImageInfo()
        if (imageInfo.width <= 1 || imageInfo.height <= 1) {
            SLog.emf(
                MODULE,
                "Invalid image size. size=%dx%d, uri=%s",
                imageInfo.width, imageInfo.height, request.uri
            )
            throw DecodeException("Invalid image size. size=${imageInfo.width}x${imageInfo.height}")
        }
        val imageType = ImageType.valueOfMimeType(imageInfo.mimeType)

        val resize = request.resize
        if (canUseThumbnailMode(resize, imageInfo, imageType)) {
            // todo thumbnailMode
        } else {
            // todo normal mode
        }
        // todo maxSize, bitmapConfig, inPreferQualityOverSpeed, resize,

        TODO("Not yet implemented")
    }

    private fun readImageInfo(): ImageInfo {
        val boundOptions = BitmapFactory.Options()
        boundOptions.inJustDecodeBounds = true
        source.decodeBitmap(boundOptions)
        val exifOrientation: Int = imageOrientationCorrector
            .readExifOrientation(boundOptions.outMimeType, source)
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
            extras: RequestExtras<ImageRequest, ImageResult>?,
            source: DataSource,
        ): Decoder? = if (request is LoadableRequest) {
            BitmapFactoryDecoder(sketch, request, extras, source)
        } else {
            null
        }
    }
}