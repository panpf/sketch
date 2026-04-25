package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.util.FetcherProvider

class DoNothingFetcherProvider : FetcherProvider {

    override fun factory(context: PlatformContext): Fetcher.Factory {
        return DoNothingFetcher.Factory()
    }
}