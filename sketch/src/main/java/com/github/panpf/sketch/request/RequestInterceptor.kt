package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageData
import com.github.panpf.sketch.request.internal.ImageRequest

interface RequestInterceptor<REQUEST : ImageRequest, DATA : ImageData> {

    @MainThread
    suspend fun intercept(chain: Chain<REQUEST, DATA>): DATA

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val sketch: Sketch

        val initialRequest: REQUEST

        val request: REQUEST

        @MainThread
        suspend fun proceed(request: REQUEST): RESULT
    }
}