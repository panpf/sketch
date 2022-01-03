package com.github.panpf.sketch.request

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult

interface Interceptor<REQUEST : ImageRequest, RESULT : ImageResult> {

    @WorkerThread
    suspend fun intercept(sketch: Sketch, chain: Chain<REQUEST, RESULT>): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT : ImageResult> {

        val request: REQUEST

        @WorkerThread
        suspend fun proceed(sketch: Sketch, request: REQUEST): RESULT
    }
}