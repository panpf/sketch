package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.request.ImageRequest

fun interface Fetcher {

    suspend fun fetch(): FetchResult

    fun interface Factory {

        fun create(request: ImageRequest): Fetcher?
    }
}
