package com.github.panpf.sketch.core.desktop.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.maxMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformContextsDesktopTest {

    @Test
    fun testMaxMemory() {
        assertEquals(
            expected = Runtime.getRuntime().maxMemory(),
            actual = PlatformContext.INSTANCE.maxMemory(),
        )
    }

    @Test
    fun testAppCacheDirectory() {
        // TODO test
    }

    @Test
    fun testScreenSize() {
        // TODO test
    }
}