package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.saveCellularTrafficErrorImage
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular, Then can also cooperate with [saveCellularTrafficErrorImage] custom error image display
 */
class SaveCellularTrafficDisplayInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    var enabled = true

    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val sketch = chain.sketch
        val request = chain.request
        val requestDepth = request.depth ?: NETWORK
        val finalRequest = if (
            enabled
            && request.isSaveCellularTraffic
            && !request.isIgnoredSaveCellularTraffic
            && sketch.appContext.isCellularNetworkConnected()
            && requestDepth < RequestDepth.LOCAL
        ) {
            request.newDisplayRequest {
                depth(RequestDepth.LOCAL)
                setDepthFromSaveCellularTraffic()
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }

    override fun toString(): String = "SaveCellularTrafficDisplayInterceptor"
}