package com.github.panpf.sketch

import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.fetch.ResourceUriFetcher

internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        addFetcher(ResourceUriFetcher.Factory())
        addDecoder(SkiaDecoder.Factory())
    }.build()
}