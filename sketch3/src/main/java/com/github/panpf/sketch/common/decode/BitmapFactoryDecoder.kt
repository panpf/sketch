package com.github.panpf.sketch.common.decode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DecodeException
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.load.ImageInfo
import com.github.panpf.sketch.util.ExifInterface
import java.io.IOException

class BitmapFactoryDecoder(
    private val sketch: Sketch,
    private val request: LoadableRequest,
    private val extras: RequestExtras<ImageRequest, ImageResult>?,
    private val source: DataSource,
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val imageInfo = readImageInfo()
        if (imageInfo.width <= 1 || imageInfo.height <= 1) {
            throw DecodeException("Invalid image. size error: ${imageInfo.width}x${imageInfo.height}")
        }

        // todo maxSize, bitmapConfig, inPreferQualityOverSpeed, resize,

        TODO("Not yet implemented")
    }

    private fun readImageInfo(): ImageInfo {
        val boundOptions = BitmapFactory.Options()
        boundOptions.inJustDecodeBounds = true
        source.decodeBitmap(boundOptions)
        val exifOrientation: Int = source.readExifOrientation()
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

    companion object {
        const val MIME_TYPE_JPEG = "image/jpeg"
    }

    @Throws(IOException::class)
    fun DataSource.decodeBitmap(options: BitmapFactory.Options): Bitmap? {
        return newInputStream().use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }

    @Throws(IOException::class)
    fun DataSource.readExifOrientation(): Int {
        return newInputStream().use {
            val exifInterface = ExifInterface(it)
            exifInterface
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        }
    }
}