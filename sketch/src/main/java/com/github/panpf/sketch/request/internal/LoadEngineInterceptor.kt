package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.toLoadData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadEngineInterceptor : RequestInterceptor<LoadRequest, LoadData> {

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<LoadRequest, LoadData>): LoadData =
        withContext(Dispatchers.IO) {
            val sketch = chain.sketch
            val request = chain.request
            BitmapDecodeInterceptorChain(
                initialRequest = chain.initialRequest,
                interceptors = sketch.bitmapDecodeInterceptors,
                index = 0,
                sketch = sketch,
                request = request,
                fetchResult = null,
            ).proceed(request).toLoadData()
        }

    override fun toString(): String = "LoadEngineInterceptor"
}