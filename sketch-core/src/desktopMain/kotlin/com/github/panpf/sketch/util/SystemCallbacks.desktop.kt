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
 * Create an instance of [SystemCallbacks] for desktop platforms
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.SystemCallbacksDesktopTest.testSystemCallbacks
 */
internal actual fun SystemCallbacks(sketch: Sketch): SystemCallbacks =
    DesktopSystemCallbacks(sketch)

/**
 * Noop implementation of [SystemCallbacks]
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.SystemCallbacksDesktopTest.testDesktopSystemCallbacks
 */
internal class DesktopSystemCallbacks(val sketch: Sketch) : SystemCallbacks {

    private val _isShutdown = atomic(false)

    override val isCellularNetworkConnected get() = false
    override var isShutdown: Boolean by _isShutdown

    override fun register() {
        // Desktop platform cannot determine whether it is a metered network.
        // Desktop platforms also cannot monitor memory pressure cheaply.
    }

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
    }
}
