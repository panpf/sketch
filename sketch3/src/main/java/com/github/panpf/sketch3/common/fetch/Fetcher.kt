package com.github.panpf.sketch3.common.fetch

import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.ImageRequest

fun interface Fetcher {

    suspend fun fetch(): FetchResult?

    fun interface Factory {

        fun create(sketch3: Sketch3, request: ImageRequest): Fetcher?
    }
}
