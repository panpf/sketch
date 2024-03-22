package com.github.panpf.sketch

import com.github.panpf.sketch.decode.SkiaDecoder

internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        addDecoder(SkiaDecoder.Factory())
    }.build()
}