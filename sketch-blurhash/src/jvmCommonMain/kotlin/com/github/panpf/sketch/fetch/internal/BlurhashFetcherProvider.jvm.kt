package com.github.panpf.sketch.fetch.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.fetch.BlurhashUriFetcher
import com.github.panpf.sketch.util.FetcherProvider

@Keep
actual class BlurhashUriFetcherProvider : FetcherProvider {
    actual override fun factory(context: com.github.panpf.sketch.PlatformContext): BlurhashUriFetcher.Factory {
        return BlurhashUriFetcher.Factory()
    }
}