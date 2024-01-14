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
import android.content.res.Configuration
import android.net.ConnectivityManager.NetworkCallback
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

internal actual fun SystemCallbacks(): SystemCallbacks = AndroidSystemCallbacks()

/**
 * Proxies [ComponentCallbacks2] and [NetworkCallback]. Clear memory cache when system memory is low, and monitor network connection status
 */
class AndroidSystemCallbacks : SystemCallbacks {

    private var context: PlatformContext? = null
    private var sketchReference: WeakReference<Sketch>? = null
    private var networkObserver: NetworkObserver? = null
    private val _isShutdown = AtomicBoolean(false)
    private val componentCallbacks2 = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
        }

        override fun onLowMemory() {
            val sketch = sketchReference?.get() ?: return
            sketch.memoryCache.clear()
            // TODO bitmapPool
//            sketch.bitmapPool.clear()
        }

        override fun onTrimMemory(level: Int) {
            val sketch = sketchReference?.get() ?: return
            if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
                sketch.memoryCache.trim(0L)
            } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
                sketch.memoryCache.trim(sketch.memoryCache.size / 2)
            }
            // TODO bitmapPool
//            sketch.bitmapPool.trim(level)
        }
    }

    override val isShutdown: Boolean get() = _isShutdown.get()

    override val isCellularNetworkConnected: Boolean
        get() = networkObserver?.isCellularNetworkConnected != false

    override fun register(sketch: Sketch) {
        this.context = sketch.context
        this.sketchReference = WeakReference(sketch)
        this.networkObserver = NetworkObserver(sketch.context)
        sketch.context.registerComponentCallbacks(componentCallbacks2)
    }

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
        context?.unregisterComponentCallbacks(componentCallbacks2)
        networkObserver?.shutdown()
    }
}