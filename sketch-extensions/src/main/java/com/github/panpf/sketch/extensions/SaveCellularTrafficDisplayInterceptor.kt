package com.github.panpf.sketch.extensions

import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular, Then can also cooperate with [saveCellularTrafficErrorImage] custom error image display
 */
class SaveCellularTrafficDisplayInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    companion object {
        const val KEY = "sketch#SaveCellularTraffic"
        const val ENABLED_KEY = "sketch#enabledSaveCellularTraffic"
        const val IGNORE_KEY = "sketch#ignoreSaveCellularTraffic"
    }

    var enabled = true

    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val sketch = chain.sketch
        val request = chain.request
        val finalRequest = if (
            enabled
            && request.isSaveCellularTraffic
            && !request.isIgnoredSaveCellularTraffic
            && sketch.appContext.isCellularNetworkConnected()
            && request.depth < RequestDepth.LOCAL
        ) {
            request.newDisplayRequest {
                depth(RequestDepth.LOCAL)
                depthFrom(KEY)
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }
}

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