package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurhashDataSource

class BlurhashDecoder(
    requestContext: RequestContext,
    dataSource: BlurhashDataSource,
) : HelperDecoder(requestContext, dataSource, {
    BlurhashDecodeHelper(requestContext.request, dataSource)
}) {

    class Factory : Decoder.Factory {

        override val key: String = "BlurhashHelperDecoder"

        override fun create(
            requestContext: RequestContext, fetchResult: FetchResult
        ): BlurhashDecoder? {
            if (!isApplicable(fetchResult)) return null
            return BlurhashDecoder(
                requestContext = requestContext, dataSource = fetchResult.dataSource as BlurhashDataSource
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