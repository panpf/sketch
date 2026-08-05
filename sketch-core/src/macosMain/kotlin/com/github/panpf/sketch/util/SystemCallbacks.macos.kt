/*
 * Copyright (C) 2026 panpf <panpfpanpf@outlook.com>
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

/** Create an instance of [SystemCallbacks] for macOS. */
internal actual fun SystemCallbacks(sketch: Sketch): SystemCallbacks =
    MacosSystemCallbacks(sketch)

/**
 * macOS does not expose a cheap, process-local metered-network or memory-pressure callback that
 * matches Sketch's cross-platform contract, so these callbacks intentionally remain inactive.
 */
internal class MacosSystemCallbacks(val sketch: Sketch) : SystemCallbacks {

    private val _isShutdown = atomic(false)

    override val isCellularNetworkConnected: Boolean
        get() = false

    override var isShutdown: Boolean by _isShutdown

    override fun register() = Unit

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
    }
}
