package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras

interface DecodeInterceptor<REQUEST : ImageRequest, RESULT> {

    @WorkerThread
    suspend fun intercept(chain: Chain<REQUEST, RESULT>): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val sketch: Sketch

        val request: REQUEST

        val requestExtras: RequestExtras

        val fetchResult: FetchResult?

        @WorkerThread
        suspend fun proceed(): RESULT
    }
}