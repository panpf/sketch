package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.SkiaGifDecoder

actual fun platformGifDecoders(): List<Decoder.Factory> {
    return listOf(SkiaGifDecoder.Factory())
}