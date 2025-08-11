package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.fetch.BlurHashUriFetcher

actual class BlurHashUriFetcherProvider : com.github.panpf.sketch.util.FetcherProvider {
    actual override fun factory(context: com.github.panpf.sketch.PlatformContext): BlurHashUriFetcher.Factory {
        return BlurHashUriFetcher.Factory()
    }
}