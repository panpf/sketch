package com.github.panpf.sketch.request

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate

interface Interceptor<REQUEST : ImageRequest, RESULT : ImageResult> {

    @WorkerThread
    suspend fun intercept(
        sketch: Sketch,
        chain: Chain<REQUEST, RESULT>,
        httpFetchProgressListenerDelegate: ProgressListenerDelegate<REQUEST>?
    ): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT : ImageResult> {

        val request: REQUEST

        @WorkerThread
        suspend fun proceed(
            sketch: Sketch,
            request: REQUEST,
            httpFetchProgressListenerDelegate: ProgressListenerDelegate<REQUEST>?
        ): RESULT
    }
}