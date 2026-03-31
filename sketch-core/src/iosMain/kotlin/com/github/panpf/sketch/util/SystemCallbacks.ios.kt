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
import com.github.panpf.sketch.util.IosMemoryPressureObserver.MemoryPressure
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Create an instance of [SystemCallbacks] for ios platforms
 *
 * @see com.github.panpf.sketch.core.ios.test.util.SystemCallbacksIosTest.testSystemCallbacks
 */
internal actual fun SystemCallbacks(sketch: Sketch): SystemCallbacks = IosSystemCallbacks(sketch)

/**
 * Noop implementation of [SystemCallbacks]
 *
 * @see com.github.panpf.sketch.core.ios.test.util.SystemCallbacksIosTest.testIosSystemCallbacks
 */
internal class IosSystemCallbacks(val sketch: Sketch) : SystemCallbacks {

    private val memoryPressureObserver = IosMemoryPressureObserver()
    private val cellularConnectivityObserver = IosCellularConnectivityObserver()
    private var scope: CoroutineScope? = null
    private var _isCellularNetworkConnected = false
    private val _isShutdown = atomic(false)

    override val isCellularNetworkConnected
        get() = _isCellularNetworkConnected

    override var isShutdown: Boolean by _isShutdown

    override fun register() {
        val scope = CoroutineScope(Dispatchers.Main)
        this.scope = scope

        scope.launch {
            cellularConnectivityObserver.flow.collect {
                _isCellularNetworkConnected = it
                sketch.logger.d { "IosSystemCallbacks. isCellularNetworkConnected: $it" }
            }
        }
        scope.launch {
            memoryPressureObserver.flow.collect {
                val memoryCache = sketch.memoryCache
                val oldSize = memoryCache.size
                when (it) {
                    MemoryPressure.CRITICAL -> memoryCache.trim(memoryCache.size / 2)
                    MemoryPressure.WARN -> memoryCache.clear()
                    else -> {}
                }
                sketch.logger.d {
                    val currentSize = memoryCache.size
                    val clearedSize = oldSize - currentSize
                    "IosSystemCallbacks. MemoryPressure $it. " +
                            "cleared ${clearedSize.formatFileSize()}, " +
                            "current ${currentSize.formatFileSize()}"
                }
            }
        }
    }

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
        scope?.cancel()
    }
}
