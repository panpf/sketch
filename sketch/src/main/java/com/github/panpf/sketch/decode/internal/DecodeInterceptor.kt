package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.ImageRequest

interface DecodeInterceptor<REQUEST : ImageRequest, RESULT> {

    @WorkerThread
    suspend fun intercept(chain: Chain<REQUEST, RESULT>): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val sketch: Sketch

        val initialRequest: REQUEST

        val request: REQUEST

        val fetchResult: FetchResult?

        @WorkerThread
        suspend fun proceed(request: REQUEST): RESULT
    }
}