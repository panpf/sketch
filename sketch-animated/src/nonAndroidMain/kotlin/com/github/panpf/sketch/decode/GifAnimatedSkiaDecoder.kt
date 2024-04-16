package com.github.panpf.sketch.decode

import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.decode.internal.AnimatedSkiaDecoder
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

class GifAnimatedSkiaDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : AnimatedSkiaDecoder(requestContext, dataSource) {

    class Factory : Decoder.Factory {

        override val key: String = "GifAnimatedSkiaDecoder"

        override fun create(requestContext: RequestContext, fetchResult: FetchResult): Decoder? {
            if (!requestContext.request.disallowAnimatedImage) {
                val imageFormat = ImageFormat.parseMimeType(fetchResult.mimeType)
                val isGif = imageFormat == ImageFormat.GIF || fetchResult.headerBytes.isGif()
                if (isGif) {
                    return GifAnimatedSkiaDecoder(requestContext, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifAnimatedSkiaDecoder"

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
    }
}