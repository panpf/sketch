package com.github.panpf.sketch.extensions

import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException

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

fun DisplayRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(PauseLoadWhenScrollingDisplayInterceptor.ENABLED_KEY, true, null)
    } else {
        removeParameter(PauseLoadWhenScrollingDisplayInterceptor.ENABLED_KEY)
    }
}

val ImageRequest.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PauseLoadWhenScrollingDisplayInterceptor.ENABLED_KEY) == true


fun DisplayRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(PauseLoadWhenScrollingDisplayInterceptor.IGNORE_KEY, true, null)
    } else {
        removeParameter(PauseLoadWhenScrollingDisplayInterceptor.IGNORE_KEY)
    }
}

val ImageRequest.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PauseLoadWhenScrollingDisplayInterceptor.IGNORE_KEY) == true


val ImageRequest.isDepthFromPauseLoadWhenScrolling: Boolean
    get() = depthFrom == PauseLoadWhenScrollingDisplayInterceptor.KEY

val SketchException.isCausedByPauseLoadWhenScrolling: Boolean
    get() = this is RequestDepthException
            && depth == RequestDepth.MEMORY
            && depthFrom == PauseLoadWhenScrollingDisplayInterceptor.KEY