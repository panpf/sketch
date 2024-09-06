package com.github.panpf.sketch.core.desktop.test.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.defaultMemoryCacheSize
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheDesktopTest {

    @Test
    fun testDefaultMemoryCacheSize() {
        assertEquals(
            expected = 256L * 1024 * 1024,
            actual = PlatformContext.INSTANCE.defaultMemoryCacheSize(),
        )
    }
}