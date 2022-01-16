package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.RequestInterceptor.Chain

class PauseLoadWhenScrollingDisplayInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    companion object {
        var scrolling = false
    }

    var enabled = true

    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
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
                setDepthFromPauseLoadWhenScrolling()
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }
}