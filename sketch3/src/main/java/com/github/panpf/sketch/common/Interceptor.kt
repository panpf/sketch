package com.github.panpf.sketch.common

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch

interface Interceptor<REQUEST : ImageRequest, RESULT : ImageResult> {

    @WorkerThread
    suspend fun intercept(
        sketch: Sketch,
        chain: Chain<REQUEST, RESULT>,
        extras: RequestExtras<REQUEST, RESULT>?
    ): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT : ImageResult> {

        val request: REQUEST

        @WorkerThread
        suspend fun proceed(
            sketch: Sketch,
            request: REQUEST,
            extras: RequestExtras<REQUEST, RESULT>?
        ): RESULT
    }
}