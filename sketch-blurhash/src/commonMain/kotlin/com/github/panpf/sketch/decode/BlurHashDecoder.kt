package com.github.panpf.sketch.decode

import com.github.panpf.sketch.decode.internal.BlurHashDecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurHashDataSource

/**
 * A [Decoder] that decodes images from a [com.github.panpf.sketch.source.BlurHashDataSource].
 *
 * @see com.github.panpf.sketch.blurhash.android.test.decode.BlurHashDecoderAndroidTest
 * @see com.github.panpf.sketch.blurhash.nonandroid.test.decode.BlurHashDecoderNonAndroidTest
 */
class BlurHashDecoder(
    requestContext: RequestContext,
    dataSource: BlurHashDataSource,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = {
        BlurHashDecodeHelper(requestContext = requestContext, blurHashUri = dataSource.blurHashUri)
    }
) {

    class Factory : Decoder.Factory {

        override val key: String = "BlurHashDecoder"

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

        override fun toString(): String = "BlurHashDecoder"
    }
}