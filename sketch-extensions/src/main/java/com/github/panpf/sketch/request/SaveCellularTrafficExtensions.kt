package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException

fun DisplayRequest.Builder.saveCellularTraffic(enabled: Boolean = true) = apply {
    if (enabled) {
        setParameter(SaveCellularTrafficDisplayInterceptor.ENABLED_KEY, true, null)
    } else {
        removeParameter(SaveCellularTrafficDisplayInterceptor.ENABLED_KEY)
    }
}

val ImageRequest.isSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(SaveCellularTrafficDisplayInterceptor.ENABLED_KEY) == true


fun DisplayRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(SaveCellularTrafficDisplayInterceptor.IGNORE_KEY, true, null)
    } else {
        removeParameter(SaveCellularTrafficDisplayInterceptor.IGNORE_KEY)
    }
}

val ImageRequest.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.value<Boolean>(SaveCellularTrafficDisplayInterceptor.IGNORE_KEY) == true


val ImageRequest.isDepthFromSaveCellularTraffic: Boolean
    get() = depthFrom == SaveCellularTrafficDisplayInterceptor.KEY

val SketchException.isCausedBySaveCellularTraffic: Boolean
    get() = this is RequestDepthException
            && depth == RequestDepth.LOCAL
            && depthFrom == SaveCellularTrafficDisplayInterceptor.KEY