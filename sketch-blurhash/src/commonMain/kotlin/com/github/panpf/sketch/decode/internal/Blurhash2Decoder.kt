package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.Blurhash2DataSource

class Blurhash2Decoder(
    requestContext: RequestContext,
    dataSource: Blurhash2DataSource,
) : HelperDecoder(requestContext, dataSource, {
    Blurhash2DecodeHelper(requestContext.request, dataSource)
}) {

    class Factory : Decoder.Factory {

        override val key: String = "BlurHashHelperDecoder"

        override fun create(
            requestContext: RequestContext, fetchResult: FetchResult
        ): Blurhash2Decoder? {
            if (!isApplicable(fetchResult)) return null
            return Blurhash2Decoder(
                requestContext = requestContext,
                dataSource = fetchResult.dataSource as Blurhash2DataSource
            )
        }

        private fun isApplicable(fetchResult: FetchResult): Boolean {
            return fetchResult.dataSource is Blurhash2DataSource
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