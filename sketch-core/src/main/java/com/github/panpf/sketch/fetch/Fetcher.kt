package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.request.ListenerInfo

fun interface Fetcher {

    suspend fun fetch(): FetchResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: ImageRequest,
            listenerInfo: ListenerInfo<ImageRequest, ImageResult>?
        ): Fetcher?
    }
}
