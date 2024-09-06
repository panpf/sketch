package com.github.panpf.sketch.core.desktop.test.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.defaultMemoryCacheSizePercent
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheDesktopTest {

    @Test
    fun testDefaultMemoryCacheSizePercent() {
        assertEquals(
            expected = 0.20,
            actual = PlatformContext.INSTANCE.defaultMemoryCacheSizePercent(),
            absoluteTolerance = 0.0
        )
    }
}