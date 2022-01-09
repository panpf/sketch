package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.DecodeBitmapInterceptorChain
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadData>,
    ): LoadData {
        val request = chain.request
        val bitmapDecodeResult = DecodeBitmapInterceptorChain(
            initialRequest = request,
            interceptors = sketch.decodeBitmapInterceptors,
            index = 0,
            request = request,
        ).proceed(sketch, request)
        return LoadData(bitmapDecodeResult.bitmap, bitmapDecodeResult.info, bitmapDecodeResult.from)
    }
}