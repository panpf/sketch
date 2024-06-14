package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.decode.internal.SkiaAnimatedDecoder
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

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
            if (!requestContext.request.disallowAnimatedImage) {
                val imageFormat = ImageFormat.parseMimeType(fetchResult.mimeType)
                val isGif = imageFormat == ImageFormat.GIF || fetchResult.headerBytes.isGif()
                if (isGif) {
                    return GifSkiaAnimatedDecoder(requestContext, fetchResult.dataSource)
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

        override fun toString(): String = "GifSkiaAnimatedDecoder"
    }
}