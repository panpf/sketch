package com.github.panpf.sketch

import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher


internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        addFetcher(FileUriFetcher.Factory())
        addFetcher(ResourceUriFetcher.Factory())
//        addDecoder(ImageReaderDecoder.Factory())
        addDecoder(SkiaDecoder.Factory())
    }.build()
}