package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor

class LoadEngineInterceptor : RequestInterceptor<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<LoadRequest, LoadData>): LoadData {
        val sketch = chain.sketch
        val request = chain.request
        val bitmapDecodeResult = BitmapDecodeInterceptorChain(
            initialRequest = request,
            interceptors = sketch.bitmapDecodeInterceptors,
            index = 0,
            sketch = sketch,
            request = request,
            fetchResult = null,
        ).proceed(request)
        return LoadData(bitmapDecodeResult.bitmap, bitmapDecodeResult.imageInfo, bitmapDecodeResult.dataFrom)
    }
}