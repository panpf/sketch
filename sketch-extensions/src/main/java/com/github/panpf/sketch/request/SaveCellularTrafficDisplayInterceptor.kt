package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.saveCellularTrafficError

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular,
 * Then can also cooperate with [saveCellularTrafficError] custom error image display
 *
 * @see DisplayRequest.Builder.saveCellularTraffic
 * @see ErrorStateImage.Builder.saveCellularTrafficError
 */
class SaveCellularTrafficDisplayInterceptor constructor(
    isCellularNetworkConnected: ((Sketch) -> Boolean)? = null
) : RequestInterceptor {

    companion object {
        private const val OLD_DEPTH_KEY = "SAVE_CELLULAR_TRAFFIC_OLD_DEPTH"
    }

    var enabled = true

    private val isCellularNetworkConnected: (Sketch) -> Boolean =
        isCellularNetworkConnected ?: { sketch ->
            sketch.systemCallbacks.isCellularNetworkConnected
        }

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        val finalRequest = when {
            request !is DisplayRequest -> {
                request
            }
            enabled && request.isSaveCellularTraffic
                    && !request.isIgnoredSaveCellularTraffic
                    && isCellularNetworkConnected(chain.sketch) -> {
                if (request.depth != Depth.LOCAL) {
                    val oldDepth = request.depth
                    request.newDisplayRequest {
                        depth(Depth.LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
                        setParameter(OLD_DEPTH_KEY, oldDepth.name, null)
                    }
                } else {
                    request
                }
            }
            else -> {
                val oldDepth = request.parameters?.value<String>(OLD_DEPTH_KEY)?.let {
                    try {
                        Depth.valueOf(it)
                    } catch (e: Exception) {
                        e.toString()
                        null
                    }
                }
                if (oldDepth != null && request.depth != oldDepth) {
                    request.newDisplayRequest {
                        depth(oldDepth)
                        removeParameter(OLD_DEPTH_KEY)
                    }
                } else {
                    request
                }
            }
        }
        return chain.proceed(finalRequest)
    }

    override fun toString(): String = "SaveCellularTrafficDisplayInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaveCellularTrafficDisplayInterceptor) return false

        if (enabled != other.enabled) return false

        return true
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }
}