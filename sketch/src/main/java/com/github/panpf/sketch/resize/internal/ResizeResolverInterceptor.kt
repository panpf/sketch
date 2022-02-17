package com.github.panpf.sketch.resize.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class ResizeResolverInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    @MainThread
    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val request = chain.request
        val resizeSize = request.resizeSize
        val resizeSizeResolver = request.resizeSizeResolver
        return if (resizeSize == null && resizeSizeResolver != null) {
            val newResizeSize = resizeSizeResolver.size()
            if (newResizeSize != null) {
                chain.proceed(request.newDisplayRequest { resizeSize(newResizeSize) })
            } else {
                chain.proceed(request)
            }
        } else {
            chain.proceed(request)
        }
    }
}