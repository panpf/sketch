package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageData
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras

interface RequestInterceptor<REQUEST : ImageRequest, DATA : ImageData> {

    @MainThread
    suspend fun intercept(chain: Chain<REQUEST, DATA>): DATA

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val sketch: Sketch

        val initialRequest: REQUEST

        val request: REQUEST

        val requestExtras: RequestExtras

        @MainThread
        suspend fun proceed(request: REQUEST): RESULT
    }
}