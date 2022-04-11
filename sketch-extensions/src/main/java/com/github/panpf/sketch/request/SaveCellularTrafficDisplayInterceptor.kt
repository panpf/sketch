package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular, Then can also cooperate with [saveCellularTrafficError] custom error image display
 */
class SaveCellularTrafficDisplayInterceptor : RequestInterceptor {

    var enabled = true

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        if (request !is DisplayRequest) {
            return chain.proceed(request)
        }

        val sketch = chain.sketch
        val requestDepth = request.depth
        val finalRequest = if (
            enabled
            && request.isSaveCellularTraffic
            && !request.isIgnoredSaveCellularTraffic
            && sketch.context.isCellularNetworkConnected()
            && requestDepth < RequestDepth.LOCAL
        ) {
            request.newRequest {

            }
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