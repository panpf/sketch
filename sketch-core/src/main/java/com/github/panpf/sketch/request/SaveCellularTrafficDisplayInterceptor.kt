package com.github.panpf.sketch.request

import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Interceptor.Chain
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor.Companion.ENABLED_KEY
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor.Companion.IGNORE_KEY
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor.Companion.LOCAL_DEPTH_FROM_SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular, Then can also cooperate with [com.github.panpf.sketch.stateimage.ErrorStateImage.saveCellularTrafficImage] custom error image display
 */
class SaveCellularTrafficDisplayInterceptor :
    Interceptor<DisplayRequest, DisplayData> {

    companion object {
        const val ENABLED_KEY = "sketch#enabledSaveCellularTraffic"
        const val IGNORE_KEY = "sketch#ignoreSaveCellularTraffic"
        const val LOCAL_DEPTH_FROM_SAVE_CELLULAR_TRAFFIC_KEY =
            "sketch#localDepthFromSaveCellularTraffic"
    }

    var enabled = true

    override suspend fun intercept(
        sketch: Sketch,
        chain: Chain<DisplayRequest, DisplayData>
    ): DisplayData {
        val request = chain.request
        val finalRequest = if (
            enabled
            && request.isSaveCellularTraffic
            && !request.isIgnoredSaveCellularTraffic
            && sketch.appContext.isCellularNetworkConnected()
            && request.depth <= RequestDepth.NETWORK
        ) {
            request.newDisplayRequest {
                depth(RequestDepth.LOCAL)
                setParameter(LOCAL_DEPTH_FROM_SAVE_CELLULAR_TRAFFIC_KEY, true, null)
            }
        } else {
            request
        }
        return chain.proceed(sketch, finalRequest)
    }
}

fun DisplayRequest.Builder.saveCellularTraffic(disabled: Boolean = true) = apply {
    if (disabled) {
        setParameter(ENABLED_KEY, true, null)
    } else {
        removeParameter(ENABLED_KEY)
    }
}

val Parameters.isSaveCellularTraffic: Boolean
    get() = value<Boolean>(ENABLED_KEY) == true

val ImageRequest.isSaveCellularTraffic: Boolean
    get() = parameters?.isSaveCellularTraffic == true


fun DisplayRequest.Builder.ignoreSaveCellularTraffic(ignore: Boolean = true) = apply {
    if (ignore) {
        setParameter(IGNORE_KEY, true, null)
    } else {
        removeParameter(IGNORE_KEY)
    }
}

val Parameters.isIgnoredSaveCellularTraffic: Boolean
    get() = value<Boolean>(IGNORE_KEY) == true

val ImageRequest.isIgnoredSaveCellularTraffic: Boolean
    get() = parameters?.isIgnoredSaveCellularTraffic == true


val Parameters.isLocalDepthFromSaveCellularTraffic: Boolean
    get() = value<Boolean>(LOCAL_DEPTH_FROM_SAVE_CELLULAR_TRAFFIC_KEY) == true

val ImageRequest.isLocalDepthFromSaveCellularTraffic: Boolean
    get() = parameters?.isLocalDepthFromSaveCellularTraffic == true