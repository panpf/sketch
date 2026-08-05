package com.github.panpf.sketch.core.macos.test.util

import com.github.panpf.sketch.test.singleton.getSketch
import com.github.panpf.sketch.util.MacosSystemCallbacks
import com.github.panpf.sketch.util.SystemCallbacks
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SystemCallbacksMacosTest {

    @Test
    fun testSystemCallbacks() {
        assertIs<MacosSystemCallbacks>(SystemCallbacks(getSketch()))
    }

    @Test
    fun testMacosSystemCallbacks() {
        SystemCallbacks(getSketch()).apply {
            assertFalse(isCellularNetworkConnected)
            assertFalse(isShutdown)
            register()
            shutdown()
            assertTrue(isShutdown)
            shutdown()
        }
    }
}
