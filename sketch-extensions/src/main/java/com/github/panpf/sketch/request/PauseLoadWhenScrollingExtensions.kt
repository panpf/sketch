package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException

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