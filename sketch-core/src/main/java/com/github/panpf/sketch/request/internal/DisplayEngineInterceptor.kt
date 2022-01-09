package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.DecodeDrawableInterceptorChain
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest

class DisplayEngineInterceptor : Interceptor<DisplayRequest, DisplayData> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DisplayRequest, DisplayData>,
    ): DisplayData {
        val request = chain.request
        val drawableData = DecodeDrawableInterceptorChain(
            initialRequest = request,
            interceptors = sketch.decodeDrawableInterceptors,
            index = 0,
            request = request,
        ).proceed(sketch, request)
        return DisplayData(drawableData.drawable, drawableData.info, drawableData.from)
    }
}