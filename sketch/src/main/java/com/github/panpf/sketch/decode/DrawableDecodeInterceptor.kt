package com.github.panpf.sketch.decode

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

fun interface DrawableDecodeInterceptor {

    @WorkerThread
    suspend fun intercept(chain: Chain): DrawableDecodeResult

    interface Chain {

        val sketch: Sketch

        val request: ImageRequest

        val requestContext: RequestContext

        val fetchResult: FetchResult?

        @WorkerThread
        suspend fun proceed(): DrawableDecodeResult
    }
}