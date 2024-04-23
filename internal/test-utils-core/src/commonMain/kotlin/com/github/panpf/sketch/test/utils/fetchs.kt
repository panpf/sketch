package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest

suspend fun ImageRequest.fetch(sketch: Sketch, factory: Fetcher.Factory? = null): FetchResult {
    val fetcher = factory?.create(sketch, this) ?: sketch.components.newFetcherOrThrow(this)
    return fetcher.fetch().getOrThrow()
}
