package com.github.panpf.sketch.util

import com.github.panpf.sketch.Sketch
import java.util.concurrent.atomic.AtomicBoolean

internal actual fun SystemCallbacks(): SystemCallbacks = NoopSystemCallbacks()

// TODO: Listen for memory-pressure events to trim the memory cache on non-Android platforms.
private class NoopSystemCallbacks : SystemCallbacks {

    private val _isShutdown = AtomicBoolean(false)

    override val isCellularNetworkConnected get() = false

    override var isShutdown: Boolean
        get() = _isShutdown.get()
        set(value) {
            _isShutdown.set(value)
        }

    override fun register(sketch: Sketch) {}

    override fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
    }
}
