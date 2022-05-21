package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Intercept the execution of [ImageRequest], you can change the input and output, register to [ComponentRegistry] to take effect
 */
fun interface RequestInterceptor {

    @MainThread
    suspend fun intercept(chain: Chain): ImageData

    interface Chain {

        val sketch: Sketch

        val initialRequest: ImageRequest

        val request: ImageRequest

        val requestContext: RequestContext

        /**
         * Continue executing the chain.
         *
         * @param request The request to proceed with.
         */
        @MainThread
        suspend fun proceed(request: ImageRequest): ImageData
    }
}