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
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.util.installPixels

class BlurhashHelperDecoder(
    requestContext: RequestContext,
    dataSource: BlurhashDataSource,
    decodeHelperFactory: () -> DecodeHelper,
): AbstractHelperDecoder(requestContext, dataSource, decodeHelperFactory) {

    override fun decodeFull(
        decodeHelper: DecodeHelper,
        resize: Resize
    ): Pair<Image, List<String>?> {
        val decoded = BlurhashUtil.decodeByte((dataSource as BlurhashDataSource).blurhash, resize.size.width, resize.size.height)
        val createBitmap = createBitmap(resize.size.width, resize.size.height, BASE_COLOR_TYPE)
        createBitmap.installPixels(decoded)
        return createBitmap.asImage() to null
    }

    override fun decodeRegion(
        decodeHelper: DecodeHelper,
        resize: Resize
    ): Pair<Image, List<String>?> {
        return decodeFull(decodeHelper, resize)
    }

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
                decodeHelperFactory = {object : DecodeHelper.Adapter(){
                    override val imageInfo: ImageInfo = ImageInfo(requestContext.size, "")

                } }
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
}