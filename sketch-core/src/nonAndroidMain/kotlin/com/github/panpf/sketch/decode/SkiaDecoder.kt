package com.github.panpf.sketch.decode

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.appliedExifOrientation
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.decode
import com.github.panpf.sketch.decode.internal.decodeRegion
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Rect
import okio.buffer
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image

class SkiaDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    override suspend fun decode(): Result<DecodeResult> = runCatching {
        // TODO https://github.com/JetBrains/skiko/issues/741
        val bytes = dataSource.openSource().buffer().use { it.readByteArray() }
        val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
        val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
        val image = Image.makeFromEncoded(bytes)
        val imageInfo = ImageInfo(
            width = image.width,
            height = image.height,
            mimeType = mimeType,
            // TODO Image will parse exif and does not support closing
            exifOrientation = ExifOrientation.UNDEFINED
        )
        val canDecodeRegion = true
        realDecode(
            requestContext = requestContext,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            decodeFull = { sampleSize ->
                realDecodeFull(image, sampleSize).asSketchImage()
            },
            decodeRegion = if (canDecodeRegion) { srcRect, sampleSize ->
                realDecodeRegion(image, srcRect, sampleSize).asSketchImage()
            } else null
        ).appliedExifOrientation(requestContext)
            .appliedResize(requestContext)
    }

    private fun realDecodeFull(image: Image, sampleSize: Int): SkiaBitmap {
        // TODO bitmapConfig
        return image.decode(sampleSize)
    }

    private fun realDecodeRegion(
        image: Image,
        srcRect: Rect,
        sampleSize: Int
    ): SkiaBitmap {
        // TODO bitmapConfig
        return image.decodeRegion(srcRect, sampleSize)
    }

    class Factory : Decoder.Factory {

        override val key: String get() = "SkiaDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): Decoder {
            val dataSource = fetchResult.dataSource
            return SkiaDecoder(requestContext, dataSource)
        }

        @Suppress("RedundantOverride")
        override fun equals(other: Any?): Boolean {
            // If you add construction parameters to this class, you need to change it here
            return super.equals(other)
        }

        @Suppress("RedundantOverride")
        override fun hashCode(): Int {
            // If you add construction parameters to this class, you need to change it here
            return super.hashCode()
        }

        override fun toString(): String = "SkiaDecoder"
    }
}