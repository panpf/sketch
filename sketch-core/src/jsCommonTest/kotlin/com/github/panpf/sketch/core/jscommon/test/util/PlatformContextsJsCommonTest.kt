package com.github.panpf.sketch.core.jscommon.test.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.maxMemory
import com.github.panpf.sketch.util.screenSize
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformContextsJsCommonTest {

    @Test
    fun testMaxMemory() {
        assertEquals(
            expected = 512L * 1024L * 1024L,
            actual = PlatformContext.INSTANCE.maxMemory(),
        )
    }

    @Test
    fun testAppCacheDirectory() {
        assertEquals(
            expected = null,
            actual = PlatformContext.INSTANCE.appCacheDirectory(),
        )
    }

    @Test
    fun testScreenSize() {
        assertEquals(
            expected = Size(1920, 1080),
            actual = PlatformContext.INSTANCE.screenSize(),
        )
    }
}