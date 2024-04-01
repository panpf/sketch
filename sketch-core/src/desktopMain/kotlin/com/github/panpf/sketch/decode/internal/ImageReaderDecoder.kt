//package com.github.panpf.sketch.decode.internal
//
//import com.github.panpf.sketch.asSketchImage
//import com.github.panpf.sketch.datasource.DataSource
//import com.github.panpf.sketch.decode.DecodeResult
//import com.github.panpf.sketch.decode.Decoder
//import com.github.panpf.sketch.fetch.FetchResult
//import com.github.panpf.sketch.request.internal.RequestContext
//import com.github.panpf.sketch.util.Rect
//import java.awt.Rectangle
//import java.awt.image.BufferedImage
//
//class ImageReaderDecoder(
//    private val requestContext: RequestContext,
//    private val dataSource: DataSource,
//) : Decoder {
//
//    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
//        val request = requestContext.request
//        val imageInfo =
//            dataSource.readImageInfoWithImageReaderOrThrow(request.ignoreExifOrientation)
//        val canDecodeRegion = checkSupportSubsamplingByMimeType(imageInfo.mimeType)
//        val decodeResult = realDecode(
//            requestContext = requestContext,
//            dataFrom = dataSource.dataFrom,
//            imageInfo = imageInfo,
//            decodeFull = { sampleSize ->
//                realDecodeFull(sampleSize).asSketchImage()
//            },
//            decodeRegion = if (canDecodeRegion) { srcRect, sampleSize ->
//                realDecodeRegion(srcRect, sampleSize).asSketchImage()
//            } else null
//        )
//        val exifResult = decodeResult.appliedExifOrientation(requestContext)
//        val resizedResult = exifResult.appliedResize(requestContext)
//        resizedResult
//    }
//
//    private fun realDecodeFull(sampleSize: Int): BufferedImage {
//        // TODO bitmapConfig
//        return dataSource.decodeImage {
//            setSourceSubsampling(
//                /* sourceXSubsampling = */ sampleSize,
//                /* sourceYSubsampling = */ sampleSize,
//                /* subsamplingXOffset = */ 0,
//                /* subsamplingYOffset = */ 0
//            )
//        }
//    }
//
//    private fun realDecodeRegion(
//        srcRect: Rect,
//        sampleSize: Int
//    ): BufferedImage {
//        // TODO bitmapConfig
//        return dataSource.decodeImage {
//            sourceRegion = Rectangle(
//                /* x = */ srcRect.left,
//                /* y = */ srcRect.top,
//                /* width = */ srcRect.width(),
//                /* height = */ srcRect.height()
//            )
//            setSourceSubsampling(
//                /* sourceXSubsampling = */ sampleSize,
//                /* sourceYSubsampling = */ sampleSize,
//                /* subsamplingXOffset = */ 0,
//                /* subsamplingYOffset = */ 0
//            )
//        }
//    }
//
//    class Factory : Decoder.Factory {
//
//        override val key: String get() = "ImageReaderDecoder"
//
//        override fun create(
//            requestContext: RequestContext,
//            fetchResult: FetchResult,
//        ): Decoder {
//            val dataSource = fetchResult.dataSource
//            return ImageReaderDecoder(requestContext, dataSource)
//        }
//
//        @Suppress("RedundantOverride")
//        override fun equals(other: Any?): Boolean {
//            // If you add construction parameters to this class, you need to change it here
//            return super.equals(other)
//        }
//
//        @Suppress("RedundantOverride")
//        override fun hashCode(): Int {
//            // If you add construction parameters to this class, you need to change it here
//            return super.hashCode()
//        }
//
//        override fun toString(): String = "ImageReaderDecoder"
//    }
//}