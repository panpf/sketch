package com.github.panpf.sketch.decode

import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.SkiaDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

class SkiaDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelper = SkiaDecodeHelper(requestContext.request, dataSource)
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