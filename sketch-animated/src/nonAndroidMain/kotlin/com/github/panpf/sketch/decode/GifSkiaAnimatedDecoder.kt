package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.SkiaAnimatedDecoder
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.source.DataSource

/**
 * Adds gif support by Skia
 */
fun ComponentRegistry.Builder.supportSkiaGif(): ComponentRegistry.Builder = apply {
    addDecoder(GifSkiaAnimatedDecoder.Factory())
}

class GifSkiaAnimatedDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : SkiaAnimatedDecoder(requestContext, dataSource) {

    class Factory : Decoder.Factory {

        override val key: String = "GifSkiaAnimatedDecoder"

        override fun create(requestContext: RequestContext, fetchResult: FetchResult): Decoder? {
            if (
                !requestContext.request.disallowAnimatedImage
                && fetchResult.headerBytes.isGif()
            ) {
                return GifSkiaAnimatedDecoder(requestContext, fetchResult.dataSource)
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

        override fun toString(): String = "GifSkiaAnimatedDecoder"
    }
}