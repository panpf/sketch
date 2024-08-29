/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.util.Key

/**
 * The processing depth of the request.
 *
 * @see com.github.panpf.sketch.core.common.test.request.DepthTest
 */
enum class Depth {
    /**
     * Allows loading images from memory, local, and network
     */
    NETWORK,

    /**
     * Load images only from memory or local
     */
    LOCAL,

    /**
     * Load images only from memory
     */
    MEMORY;
}

/**
 * Depth holder, used to specify the depth of the request
 *
 * @see com.github.panpf.sketch.core.common.test.request.DepthHolderTest
 */
data class DepthHolder(val depth: Depth, val from: String? = null) : Key {

    companion object {
        val Default = DepthHolder(Depth.NETWORK)
    }

    override val key: String by lazy {
        if (from != null) {
            "DepthHolder(depth=$depth,from='$from')"
        } else {
            "DepthHolder($depth)"
        }
    }

    override fun toString(): String {
        return "DepthHolder(depth=$depth, from='$from')"
    }
}