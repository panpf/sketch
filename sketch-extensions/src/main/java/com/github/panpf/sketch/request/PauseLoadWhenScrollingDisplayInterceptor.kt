package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.RequestInterceptor.Chain

class PauseLoadWhenScrollingDisplayInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    companion object {
        const val KEY = "sketch#PauseLoadWhenScrolling"
        const val ENABLED_KEY = "sketch#enabledPauseLoadWhenScrolling"
        const val IGNORE_KEY = "sketch#ignorePauseLoadWhenScrolling"
        var scrolling = false
    }

    var enabled = true

    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val sketch = chain.sketch
        val request = chain.request
        val finalRequest = if (
            enabled
            && scrolling
            && request.isPauseLoadWhenScrolling
            && !request.isIgnoredPauseLoadWhenScrolling
            && request.depth < RequestDepth.MEMORY
        ) {
            request.newDisplayRequest {
                depth(RequestDepth.MEMORY)
                depthFrom(KEY)
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }
}