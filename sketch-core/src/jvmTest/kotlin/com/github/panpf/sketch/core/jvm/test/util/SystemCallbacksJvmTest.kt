package com.github.panpf.sketch.core.jvm.test.util

import com.github.panpf.sketch.test.singleton.getSketch
import com.github.panpf.sketch.util.JvmSystemCallbacks
import com.github.panpf.sketch.util.SystemCallbacks
import kotlin.test.Test
import kotlin.test.assertEquals

class SystemCallbacksJvmTest {

    @Test
    fun testSystemCallbacks() {
        val sketch = getSketch()
        assertEquals(
            expected = true,
            actual = SystemCallbacks(sketch) is JvmSystemCallbacks
        )
    }

    @Test
    fun testJvmSystemCallbacks() {
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