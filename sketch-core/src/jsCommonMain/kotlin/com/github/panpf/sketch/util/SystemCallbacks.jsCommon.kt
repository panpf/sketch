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

package com.github.panpf.sketch.util

import com.github.panpf.sketch.Sketch
import kotlinx.atomicfu.atomic

/**
 * Create an instance of [SystemCallbacks] for js platforms
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.SystemCallbacksJsCommonTest.testSystemCallbacks
 */
internal actual fun SystemCallbacks(sketch: Sketch): SystemCallbacks = JsSystemCallbacks()

/**
 * Noop implementation of [SystemCallbacks]
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.SystemCallbacksJsCommonTest.testJsSystemCallbacks
 */
internal class JsSystemCallbacks : SystemCallbacks {

    override val isCellularNetworkConnected get() = false

    private val _isShutdown = atomic(false)
    override var isShutdown: Boolean by _isShutdown

    override fun register() {
        // TODO Listen for memory-pressure events to trim the memory cache on js platforms.
        // TODO Implement network type detection for js platforms.
        //  https://github.com/jordond/connectivity/blob/main/connectivity-http/src/commonMain/kotlin/dev/jordond/connectivity/internal/HttpConnectivity.kt
    }

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
    }
}
