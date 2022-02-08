package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheEditor
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.toDisplayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DisplayEngineInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    @WorkerThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<DisplayRequest, DisplayData>): DisplayData {
        val sketch = chain.sketch
        val request = chain.request

        newBitmapMemoryCacheEditor(sketch, request)
            ?.tryLock {
                read()
            }?.let { result ->
                return result.toDisplayData()
            }

        val loadingDrawable =
            request.placeholderImage?.getDrawable(sketch, request, null)
        withContext(Dispatchers.Main) {
            request.target.onStart(loadingDrawable)
        }

        return DrawableDecodeInterceptorChain(
            initialRequest = request,
            interceptors = sketch.drawableDecodeInterceptors,
            index = 0,
            sketch = sketch,
            request = request,
            fetchResult = null,
        ).proceed(request).toDisplayData()
    }

    override fun toString(): String = "DisplayEngineInterceptor"
}