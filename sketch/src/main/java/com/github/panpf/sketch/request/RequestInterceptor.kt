package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestContext

fun interface RequestInterceptor {

    @MainThread
    suspend fun intercept(chain: Chain): ImageData

    interface Chain {

        val sketch: Sketch

        val initialRequest: ImageRequest

        val request: ImageRequest

        val requestContext: RequestContext

        @MainThread
        suspend fun proceed(request: ImageRequest): ImageData
    }
}