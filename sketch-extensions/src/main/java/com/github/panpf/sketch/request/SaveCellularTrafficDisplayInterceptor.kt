package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.saveCellularTrafficError

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular, Then can also cooperate with [saveCellularTrafficError] custom error image display
 */
class SaveCellularTrafficDisplayInterceptor constructor(
    isCellularNetworkConnected: ((Sketch) -> Boolean)? = null
) : RequestInterceptor {

    var enabled = true

    private val isCellularNetworkConnected: (Sketch) -> Boolean =
        isCellularNetworkConnected ?: { sketch ->
            sketch.systemCallbacks.isCellularNetworkConnected
        }

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        val finalRequest = if (
            request is DisplayRequest
            && enabled
            && request.isSaveCellularTraffic
            && !request.isIgnoredSaveCellularTraffic
            && isCellularNetworkConnected(chain.sketch)
            && request.depth < Depth.LOCAL
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