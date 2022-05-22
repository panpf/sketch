package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.saveCellularTrafficError

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

        val depth = request.depth
        val finalRequest = if (
            enabled
            && request.isSaveCellularTraffic
            && !request.isIgnoredSaveCellularTraffic
            && chain.sketch.systemCallbacks.isCellularNetworkConnected
            && depth < Depth.LOCAL
        ) {
            request.newDisplayRequest {
                depth(Depth.LOCAL)
                setDepthFromSaveCellularTraffic()
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }

    override fun toString(): String = "SaveCellularTrafficDisplayInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaveCellularTrafficDisplayInterceptor

        if (enabled != other.enabled) return false

        return true
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }
}