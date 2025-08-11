package com.github.panpf.sketch.fetch.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.util.FetcherProvider

@Keep
actual class BlurHashUriFetcherProvider : FetcherProvider {
    actual override fun factory(context: com.github.panpf.sketch.PlatformContext): BlurHashUriFetcher.Factory {
        return BlurHashUriFetcher.Factory()
    }
}