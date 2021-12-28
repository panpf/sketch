package com.github.panpf.sketch.common.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ListenerInfo

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
