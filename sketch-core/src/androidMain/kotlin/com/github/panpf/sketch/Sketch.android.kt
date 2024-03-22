package com.github.panpf.sketch

import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher

internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        addFetcher(ContentUriFetcher.Factory())
        addFetcher(ResourceUriFetcher.Factory())
        addFetcher(AssetUriFetcher.Factory())
//        addFetcher(FileUriFetcher.Factory())

        addDecoder(DrawableDecoder.Factory())
        addDecoder(BitmapFactoryDecoder.Factory())
    }.build()
}