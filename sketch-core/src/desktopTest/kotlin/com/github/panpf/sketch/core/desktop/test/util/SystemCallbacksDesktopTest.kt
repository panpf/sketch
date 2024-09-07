package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.test.singleton.getSketch
import com.github.panpf.sketch.util.DesktopSystemCallbacks
import com.github.panpf.sketch.util.SystemCallbacks
import kotlin.test.Test
import kotlin.test.assertEquals

class SystemCallbacksDesktopTest {

    @Test
    fun testSystemCallbacks() {
        val sketch = getSketch()
        assertEquals(
            expected = true,
            actual = SystemCallbacks(sketch) is DesktopSystemCallbacks
        )
    }

    @Test
    fun testDesktopSystemCallbacks() {
        val sketch = getSketch()
        SystemCallbacks(sketch).apply {
            assertEquals(expected = false, actual = isCellularNetworkConnected)
            assertEquals(expected = false, actual = isShutdown)
            shutdown()
            assertEquals(expected = true, actual = isShutdown)
            shutdown()
        }
    }
}