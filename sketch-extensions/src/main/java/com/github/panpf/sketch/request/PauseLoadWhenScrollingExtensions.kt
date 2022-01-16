package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException

private const val KEY = "sketch#PauseLoadWhenScrolling"
private const val ENABLED_KEY = "sketch#enabledPauseLoadWhenScrolling"
private const val IGNORE_KEY = "sketch#ignorePauseLoadWhenScrolling"

fun DisplayRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(ENABLED_KEY, true, null)
    } else {
        removeParameter(ENABLED_KEY)
    }
}

fun DisplayOptions.Builder.pauseLoadWhenScrolling(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(ENABLED_KEY, true, null)
    } else {
        removeParameter(ENABLED_KEY)
    }
}

val DisplayRequest.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true

val DisplayOptions.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true


fun DisplayRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(IGNORE_KEY, true, null)
    } else {
        removeParameter(IGNORE_KEY)
    }
}

fun DisplayOptions.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(IGNORE_KEY, true, null)
    } else {
        removeParameter(IGNORE_KEY)
    }
}

val DisplayRequest.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true

val DisplayOptions.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true


internal fun DisplayRequest.Builder.setDepthFromPauseLoadWhenScrolling() {
    depthFrom(KEY)
}

internal fun DisplayOptions.Builder.setDepthFromPauseLoadWhenScrolling() {
    depthFrom(KEY)
}

val DisplayRequest.isDepthFromPauseLoadWhenScrolling: Boolean
    get() = depthFrom == KEY

val DisplayOptions.isDepthFromPauseLoadWhenScrolling: Boolean
    get() = depthFrom == KEY


val SketchException.isCausedByPauseLoadWhenScrolling: Boolean
    get() = this is RequestDepthException
            && depth == RequestDepth.MEMORY
            && depthFrom == KEY