package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.toLogString
import okio.buffer
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ImageReaderDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    companion object {
        const val MODULE = "ImageReaderDecoder"
    }

    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val imageInfo =
            dataSource.readImageInfoWithImageReaderOrThrow(request.ignoreExifOrientation)
        val canDecodeRegion = checkSupportSubsamplingByMimeType(imageInfo.mimeType)
        val inputStream = dataSource.openSource().buffer().inputStream()
        val imageStream = ImageIO.createImageInputStream(inputStream)
        val imageReader = ImageIO.getImageReaders(imageStream).next().apply {
            input = imageStream
        }
        imageReader.defaultReadParam
        realDecode(
            requestContext = requestContext,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            decodeFull = { sampleSize ->
                realDecodeFull(imageInfo, sampleSize).asSketchImage()
            },
            decodeRegion = if (canDecodeRegion) { srcRect, sampleSize ->
                realDecodeRegion(imageInfo, srcRect, sampleSize).asSketchImage()
            } else null
        ).appliedExifOrientation(requestContext)
            .appliedResize(requestContext)
    }

    private fun realDecodeFull(imageInfo: ImageInfo, sampleSize: Int): BufferedImage {
        val image: BufferedImage = dataSource.decodeImage {
            setSourceSubsampling(
                /* sourceXSubsampling = */ sampleSize,
                /* sourceYSubsampling = */ sampleSize,
                /* subsamplingXOffset = */ 0,
                /* subsamplingYOffset = */ 0
            )
        }
        if (image.width <= 0 || image.height <= 0) {
            requestContext.logger.e(MODULE) {
                "realDecodeFull. Invalid image. ${image.toLogString()}. ${imageInfo}. '${requestContext.logKey}'"
            }
            throw ImageInvalidException("Invalid image. size=${image.width}x${image.height}")
        } else {
            requestContext.logger.d(MODULE) {
                "realDecodeFull. successful. ${image.toLogString()}. ${imageInfo}. '${requestContext.logKey}'"
            }
        }
        return image
    }

    private fun realDecodeRegion(
        imageInfo: ImageInfo,
        srcRect: Rect,
        sampleSize: Int
    ): BufferedImage {
        val image: BufferedImage = dataSource.decodeImage {
            sourceRegion = Rectangle(
                /* x = */ srcRect.left,
                /* y = */ srcRect.top,
                /* width = */ srcRect.width(),
                /* height = */ srcRect.height()
            )
            setSourceSubsampling(
                /* sourceXSubsampling = */ sampleSize,
                /* sourceYSubsampling = */ sampleSize,
                /* subsamplingXOffset = */ 0,
                /* subsamplingYOffset = */ 0
            )
        }
        if (image.width <= 0 || image.height <= 0) {
            requestContext.logger.e(MODULE) {
                "realDecodeRegion. Invalid image. ${image.toLogString()}. ${imageInfo}. ${srcRect}. '${requestContext.logKey}'"
            }
            throw ImageInvalidException("Invalid image. size=${image.width}x${image.height}")
        } else {
            requestContext.logger.d(MODULE) {
                "realDecodeRegion. successful. ${image.toLogString()}. ${imageInfo}. ${srcRect}. '${requestContext.logKey}'"
            }
        }
        return image
    }

    class Factory : Decoder.Factory {

        override val key: String get() = "ImageReaderDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): Decoder {
            val dataSource = fetchResult.dataSource
            return ImageReaderDecoder(requestContext, dataSource)
        }

        override fun toString(): String {
            return "ImageReaderDecoder"
        }
    }
}