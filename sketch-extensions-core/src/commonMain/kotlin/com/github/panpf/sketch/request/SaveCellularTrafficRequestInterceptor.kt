/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.ErrorStateImage

/**
 * Adds save cellular traffic support
 */
fun ComponentRegistry.Builder.supportSaveCellularTraffic(): ComponentRegistry.Builder = apply {
    addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
}

/**
 * To save cellular traffic. Prohibit downloading images from the Internet if the current network is cellular,
 * Then can also cooperate with saveCellularTrafficError custom error image display
 *
 * @see ImageRequest.Builder.saveCellularTraffic
 * @see ErrorStateImage.Builder.saveCellularTrafficError
 */
class SaveCellularTrafficRequestInterceptor constructor(
    override val sortWeight: Int = 0,
    isCellularNetworkConnected: ((Sketch) -> Boolean)? = null
) : RequestInterceptor {

    override val key: String? = null

    companion object {
        private const val SAVE_CELLULAR_TRAFFIC_OLD_DEPTH_KEY =
            "sketch#save_cellular_traffic_old_depth"
    }

    var enabled = true

    private val isCellularNetworkConnected: (Sketch) -> Boolean =
        isCellularNetworkConnected ?: { sketch ->
            sketch.systemCallbacks.isCellularNetworkConnected
        }

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val finalRequest = when {
            enabled && request.isSaveCellularTraffic
                    && !request.isIgnoredSaveCellularTraffic
                    && isCellularNetworkConnected(chain.sketch) -> {
                if (request.depth != LOCAL) {
                    val oldDepth = request.depth
                    request.newRequest {
                        depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
                        setParameter(SAVE_CELLULAR_TRAFFIC_OLD_DEPTH_KEY, oldDepth.name, null)
                    }
                } else {
                    request
                }
            }

            else -> {
                val oldDepth =
                    request.parameters?.value<String>(SAVE_CELLULAR_TRAFFIC_OLD_DEPTH_KEY)?.let {
                        try {
                            Depth.valueOf(it)
                        } catch (e: Exception) {
                            e.toString()
                            null
                        }
                    }
                if (oldDepth != null && request.depth != oldDepth) {
                    request.newRequest {
                        depth(oldDepth)
                        removeParameter(SAVE_CELLULAR_TRAFFIC_OLD_DEPTH_KEY)
                    }
                } else {
                    request
                }
            }
        }
        return chain.proceed(finalRequest)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaveCellularTrafficRequestInterceptor) return false
        if (sortWeight != other.sortWeight) return false
        return true
    }

    override fun hashCode(): Int {
        return sortWeight
    }

    override fun toString(): String =
        "SaveCellularTrafficDisplayInterceptor(sortWeight=$sortWeight,enabled=$enabled)"
}