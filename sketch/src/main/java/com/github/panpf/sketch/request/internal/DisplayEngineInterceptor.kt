package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheHelper
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
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

        newBitmapMemoryCacheHelper(sketch, request)?.read()?.let { result ->
            val drawable = result.drawable
            if (drawable is SketchCountBitmapDrawable) {
                val key = request.key
                chain.requestExtras.putCountDrawablePendingManagerKey(key)
                sketch.countDrawablePendingManager.mark("DefaultDrawableDecoder", key, drawable)
            }
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
                interceptors = sketch.drawableDecodeInterceptors,
                index = 0,
                sketch = sketch,
                request = request,
                requestExtras = chain.requestExtras,
                fetchResult = null,
            ).proceed().toDisplayData()
        }
    }

    override fun toString(): String = "DisplayEngineInterceptor"
}