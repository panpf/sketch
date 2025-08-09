package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.fetch.BlurhashUriFetcher

actual class BlurhashUriFetcherProvider : com.github.panpf.sketch.util.FetcherProvider {
    actual override fun factory(context: com.github.panpf.sketch.PlatformContext): BlurhashUriFetcher.Factory {
        return BlurhashUriFetcher.Factory()
    }
}