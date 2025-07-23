package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.BASE_COLOR_TYPE
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.BlurhashUtil
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.installPixels

class BlurhashHelperDecoder(
    requestContext: RequestContext,
    dataSource: BlurhashDataSource,
    decodeHelperFactory: () -> DecodeHelper,
) : HelperDecoder(requestContext, dataSource, decodeHelperFactory) {

//    override fun decodeFull(
//        decodeHelper: DecodeHelper,
//        resize: Resize
//    ): Pair<Image, List<String>?> {
//        val decoded = BlurhashUtil.decodeByte((dataSource as BlurhashDataSource).blurhash, resize.size.width, resize.size.height)
//        val createBitmap = createBitmap(resize.size.width, resize.size.height, BASE_COLOR_TYPE)
//        createBitmap.installPixels(decoded)
//        return createBitmap.asImage() to null
//    }
//
//    override fun decodeRegion(
//        decodeHelper: DecodeHelper,
//        resize: Resize
//    ): Pair<Image, List<String>?> {
//        return decodeFull(decodeHelper, resize)
//    }

    override val imageInfo: ImageInfo = ImageInfo(100, 100, "")

    class Factory : Decoder.Factory {

        override val key: String = "BlurhashHelperDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BlurhashHelperDecoder? {
            if (!isApplicable(fetchResult)) return null
            return BlurhashHelperDecoder(
                requestContext = requestContext,
                dataSource = fetchResult.dataSource as BlurhashDataSource,
                decodeHelperFactory = {
                    BlurhashDecodeHelper(requestContext.size, fetchResult.dataSource as BlurhashDataSource)
//                    object : DecodeHelper.Adapter(){
//                    override val imageInfo: ImageInfo = ImageInfo(requestContext.size, "")
//
//                }
                }
            )
        }

        private fun isApplicable(fetchResult: FetchResult): Boolean {
            return fetchResult.dataSource is BlurhashDataSource
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "BlurhashHelperDecoder"
    }

    class BlurhashDecodeHelper(size: Size, val dataSource: BlurhashDataSource) : DecodeHelper {
        override val imageInfo: ImageInfo = ImageInfo(size, "")
        override val supportRegion: Boolean = false

        override fun decode(sampleSize: Int): Image {
            val pixelData = BlurhashUtil.decodeByte(dataSource.blurhash, imageInfo.width, imageInfo.height)
            val bitmap = createBitmap(imageInfo.width, imageInfo.height, BASE_COLOR_TYPE)
            bitmap.installPixels(pixelData)
            return bitmap.asImage()
        }

        override fun decodeRegion(
            region: Rect,
            sampleSize: Int
        ): Image {
            throw UnsupportedOperationException("Decoding not implemented yet.")
        }

        override fun close() {

        }
    }
}