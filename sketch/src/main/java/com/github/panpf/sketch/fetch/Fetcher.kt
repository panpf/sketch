package com.github.panpf.sketch.fetch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.request.ImageRequest

/**
 * [Fetcher] get the data stream from the uri of [ImageRequest] and wrap it as a [FetchResult] return
 * for use by [BitmapDecoder] or [DrawableDecoder]
 */
fun interface Fetcher {

    /**
     * Get the data stream from the uri of [ImageRequest] and wrap it as a [FetchResult] return
     */
    @WorkerThread
    suspend fun fetch(): FetchResult

    /**
     * [Factory] will be registered in [ComponentRegistry], and will traverse [Factory]
     * to create [Fetcher] when it needs to extract [ImageRequest] data
     */
    fun interface Factory {

        /**
         * If the current [Factory]'s [Fetcher] can extract data from the current [request],
         * create a [Fetcher] and return it, otherwise return null
         */
        fun create(sketch: Sketch, request: ImageRequest): Fetcher?
    }
}
