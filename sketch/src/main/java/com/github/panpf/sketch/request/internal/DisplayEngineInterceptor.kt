package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor

class DisplayEngineInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    @WorkerThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<DisplayRequest, DisplayData>): DisplayData {
        val sketch = chain.sketch
        val request = chain.request
        val drawableDecodeResult = DrawableDecodeInterceptorChain(
            initialRequest = request,
            interceptors = sketch.drawableDecodeInterceptors,
            index = 0,
            sketch = sketch,
            request = request,
            fetchResult = null,
        ).proceed(request)
        return DisplayData(drawableDecodeResult.drawable, drawableDecodeResult.imageInfo, drawableDecodeResult.dataFrom)
    }

    override fun toString(): String = "DisplayEngineInterceptor"
}