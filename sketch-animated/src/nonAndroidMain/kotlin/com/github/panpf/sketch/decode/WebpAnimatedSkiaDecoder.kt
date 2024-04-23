package com.github.panpf.sketch.decode

import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.decode.internal.AnimatedSkiaDecoder
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.isAnimatedWebP
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

class WebpAnimatedSkiaDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : AnimatedSkiaDecoder(requestContext, dataSource) {

    class Factory : Decoder.Factory {

        override val key: String = "WebpAnimatedSkiaDecoder"

        override fun create(requestContext: RequestContext, fetchResult: FetchResult): Decoder? {
            if (!requestContext.request.disallowAnimatedImage) {
                val imageFormat = ImageFormat.parseMimeType(fetchResult.mimeType)
                val isAnimatedWebp =
                    (imageFormat == null || imageFormat == ImageFormat.WEBP) && fetchResult.headerBytes.isAnimatedWebP()
                if (isAnimatedWebp) {
                    return WebpAnimatedSkiaDecoder(requestContext, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "WebpAnimatedSkiaDecoder"
    }
}