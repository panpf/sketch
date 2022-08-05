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
package com.github.panpf.sketch.util

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager.NetworkCallback
import com.github.panpf.sketch.Sketch
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Proxies [ComponentCallbacks2] and [NetworkCallback]. Clear memory cache when system memory is low, and monitor network connection status
 */
class SystemCallbacks(
    private val context: Context,
    private val sketch: WeakReference<Sketch>,
) {

    private val networkObserver by lazy { NetworkObserver(context) }
    private val _isShutdown = AtomicBoolean(false)
    private val componentCallbacks2 = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
        }

        override fun onLowMemory() {
            sketch.get()?.memoryCache?.clear()
            sketch.get()?.bitmapPool?.clear()
        }

        override fun onTrimMemory(level: Int) {
            sketch.get()?.memoryCache?.trim(level)
            sketch.get()?.bitmapPool?.trim(level)
        }
    }

    init {
        context.registerComponentCallbacks(componentCallbacks2)
    }

    val isShutdown: Boolean get() = _isShutdown.get()

    val isCellularNetworkConnected: Boolean
        get() = networkObserver.isCellularNetworkConnected

    fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
        context.unregisterComponentCallbacks(componentCallbacks2)
        networkObserver.shutdown()
    }
}