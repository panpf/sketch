package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheEditor
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestDepth.MEMORY
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.toDisplayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DisplayEngineInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<DisplayRequest, DisplayData>): DisplayData {
        val sketch = chain.sketch
        val request = chain.request

        newBitmapMemoryCacheEditor(sketch, request)
            ?.tryLock {
                read()
            }?.let { result ->
                return result.toDisplayData()
            }

        val placeholderDrawable =
            request.placeholderImage?.getDrawable(sketch, request, null)
        request.target.onStart(placeholderDrawable)

        val requestDepth = request.depth
        if (requestDepth != null && requestDepth >= MEMORY) {
            throw RequestDepthException(request, requestDepth, request.depthFrom)
        }

        return withContext(Dispatchers.IO) {
            DrawableDecodeInterceptorChain(
                initialRequest = chain.initialRequest,
                interceptors = sketch.drawableDecodeInterceptors,
                index = 0,
                sketch = sketch,
                request = request,
                fetchResult = null,
            ).proceed(request).toDisplayData()
        }
    }

    override fun toString(): String = "DisplayEngineInterceptor"
}