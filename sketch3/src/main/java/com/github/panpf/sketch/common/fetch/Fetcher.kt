package com.github.panpf.sketch.common.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageData
import com.github.panpf.sketch.common.ProgressListener
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.RequestExtras

fun interface Fetcher {

    suspend fun fetch(): FetchResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: ImageRequest,
            extras: RequestExtras<ImageRequest, ImageData>?
        ): Fetcher?
    }
}
