package com.github.panpf.sketch

import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.fetch.KotlinResourceUriFetcher

internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        addFetcher(KotlinResourceUriFetcher.Factory())
        addDecoder(SkiaDecoder.Factory())
    }.build()
}