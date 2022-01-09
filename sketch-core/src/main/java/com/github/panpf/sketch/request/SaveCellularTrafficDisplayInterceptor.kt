package com.github.panpf.sketch.request

import android.content.Context
import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Interceptor.Chain
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular, Then can also cooperate with [com.github.panpf.sketch.stateimage.ErrorStateImage.saveCellularTrafficImage] custom error image display
 */
class SaveCellularTrafficDisplayInterceptor(context: Context) :
    Interceptor<DisplayRequest, DisplayData> {

    private val appContext = context.applicationContext

    var enabled = true

    override suspend fun intercept(
        sketch: Sketch,
        chain: Chain<DisplayRequest, DisplayData>
    ): DisplayData {
        val request = chain.request
        val finalRequest =
            if (enabled && appContext.isCellularNetworkConnected() && request.depth <= RequestDepth.NETWORK) {
                request.newDisplayRequest {
                    depth(RequestDepth.LOCAL)
                    parameters(
                        (request.parameters?.newBuilder() ?: Parameters.Builder())
                            .set("depthFrom", "SaveCellularTrafficDisplayInterceptor", null)
                            .build()
                    )
                }
            } else {
                request
            }
        return chain.proceed(sketch, finalRequest)
    }
}

fun ImageRequest.isDepthFromSaveCellularTrafficDisplayInterceptor(): Boolean {
    return parameters?.value<String>("depthFrom") == "SaveCellularTrafficDisplayInterceptor"
}