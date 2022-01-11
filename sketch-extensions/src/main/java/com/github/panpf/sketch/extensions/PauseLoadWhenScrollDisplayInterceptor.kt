package com.github.panpf.sketch.extensions

import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Interceptor.Chain
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestDepth.MEMORY
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException

class PauseLoadWhenScrollDisplayInterceptor : Interceptor<DisplayRequest, DisplayData> {

    companion object {
        const val KEY = "sketch#PauseLoadWhenScroll"
        const val ENABLED_KEY = "sketch#enabledSaveCellularTraffic"
        const val IGNORE_KEY = "sketch#ignorePauseLoadWhenScroll"
        var scrolling = false
    }

    var enabled = true

    override suspend fun intercept(
        sketch: Sketch,
        chain: Chain<DisplayRequest, DisplayData>
    ): DisplayData {
        val request = chain.request
        val finalRequest = if (
            enabled
            && scrolling
            && request.isPauseLoadWhenScroll
            && !request.isIgnoredPauseLoadWhenScroll
            && request.depth < MEMORY
        ) {
            request.newDisplayRequest {
                depth(MEMORY)
                depthFrom(KEY)
            }
        } else {
            request
        }
        return chain.proceed(sketch, finalRequest)
    }
}

fun DisplayRequest.Builder.pauseLoadWhenScroll(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(PauseLoadWhenScrollDisplayInterceptor.ENABLED_KEY, true, null)
    } else {
        removeParameter(PauseLoadWhenScrollDisplayInterceptor.ENABLED_KEY)
    }
}

val ImageRequest.isPauseLoadWhenScroll: Boolean
    get() = parameters?.value<Boolean>(PauseLoadWhenScrollDisplayInterceptor.ENABLED_KEY) == true


fun DisplayRequest.Builder.ignorePauseLoadWhenScroll(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(PauseLoadWhenScrollDisplayInterceptor.IGNORE_KEY, true, null)
    } else {
        removeParameter(PauseLoadWhenScrollDisplayInterceptor.IGNORE_KEY)
    }
}

val ImageRequest.isIgnoredPauseLoadWhenScroll: Boolean
    get() = parameters?.value<Boolean>(PauseLoadWhenScrollDisplayInterceptor.IGNORE_KEY) == true


val ImageRequest.isDepthFromPauseLoadWhenScroll: Boolean
    get() = depthFrom == PauseLoadWhenScrollDisplayInterceptor.KEY

val SketchException.isCausedByPauseLoadWhenScroll: Boolean
    get() = this is RequestDepthException && depth == MEMORY && depthFrom == PauseLoadWhenScrollDisplayInterceptor.KEY