package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest

fun interface Fetcher {

    suspend fun fetch(): FetchResult

    fun interface Factory {

        fun create(sketch: Sketch, request: ImageRequest): Fetcher?
    }
}