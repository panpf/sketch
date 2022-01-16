package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException

private const val KEY = "sketch#SaveCellularTraffic"
private const val ENABLED_KEY = "sketch#enabledSaveCellularTraffic"
private const val IGNORE_KEY = "sketch#ignoreSaveCellularTraffic"

fun DisplayRequest.Builder.saveCellularTraffic(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(ENABLED_KEY, true, null)
    } else {
        removeParameter(ENABLED_KEY)
    }
}

fun DisplayOptions.Builder.saveCellularTraffic(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(ENABLED_KEY, true, null)
    } else {
        removeParameter(ENABLED_KEY)
    }
}

val DisplayRequest.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true

val DisplayOptions.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(ENABLED_KEY) == true


fun DisplayRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(IGNORE_KEY, true, null)
    } else {
        removeParameter(IGNORE_KEY)
    }
}

fun DisplayOptions.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(IGNORE_KEY, true, null)
    } else {
        removeParameter(IGNORE_KEY)
    }
}

val DisplayRequest.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true

val DisplayOptions.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(IGNORE_KEY) == true


internal fun DisplayRequest.Builder.setDepthFromSaveCellularTraffic() {
    depthFrom(KEY)
}

internal fun DisplayOptions.Builder.setDepthFromSaveCellularTraffic() {
    depthFrom(KEY)
}

val DisplayRequest.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == KEY

val DisplayOptions.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == KEY


val SketchException.isCausedBySaveCellularTraffic: Boolean
    get() = this is RequestDepthException
            && depth == RequestDepth.LOCAL
            && depthFrom == KEY