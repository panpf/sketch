package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurHashDataSource

class BlurHashDecoder(
    requestContext: RequestContext,
    dataSource: BlurHashDataSource,
) : HelperDecoder(requestContext, dataSource, {
    BlurHashDecodeHelper(requestContext.request, dataSource)
}) {

    class Factory : Decoder.Factory {

        override val key: String = "BlurHashHelperDecoder"

        override fun create(
            requestContext: RequestContext, fetchResult: FetchResult
        ): BlurHashDecoder? {
            if (!isApplicable(fetchResult)) return null
            return BlurHashDecoder(
                requestContext = requestContext,
                dataSource = fetchResult.dataSource as BlurHashDataSource
            )
        }

        private fun isApplicable(fetchResult: FetchResult): Boolean {
            return fetchResult.dataSource is BlurHashDataSource
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "BlurHashHelperDecoder"
    }

}