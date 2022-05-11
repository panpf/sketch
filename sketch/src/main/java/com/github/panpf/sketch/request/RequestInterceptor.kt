package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestExtras

interface RequestInterceptor {

    @MainThread
    suspend fun intercept(chain: Chain): ImageData

    interface Chain {

        val sketch: Sketch

        val initialRequest: ImageRequest

        val request: ImageRequest

        val requestExtras: RequestExtras

        @MainThread
        suspend fun proceed(request: ImageRequest): ImageData
    }
}