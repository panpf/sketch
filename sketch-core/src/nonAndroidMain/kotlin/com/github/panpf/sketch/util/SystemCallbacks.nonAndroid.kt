package com.github.panpf.sketch.util

import com.github.panpf.sketch.Sketch
import kotlinx.atomicfu.atomic

internal actual fun SystemCallbacks(sketch: Sketch): SystemCallbacks = NoopSystemCallbacks()

private class NoopSystemCallbacks : SystemCallbacks {

    override val isCellularNetworkConnected get() = false

    private val _isShutdown = atomic(false)
    override var isShutdown: Boolean by _isShutdown

    // TODO Listen for memory-pressure events to trim the memory cache on non-Android platforms.
    override fun register() {

    }

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
    }
}
