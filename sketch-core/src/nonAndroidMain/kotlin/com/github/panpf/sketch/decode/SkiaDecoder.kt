package com.github.panpf.sketch.decode

import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.SkiaDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.source.DataSource

class SkiaDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = { SkiaDecodeHelper(requestContext.request, dataSource) }
) {

    class Factory : Decoder.Factory {

        override val key: String get() = "SkiaDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): Decoder {
            val dataSource = fetchResult.dataSource
            return SkiaDecoder(requestContext, dataSource)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "SkiaDecoder"
    }
}