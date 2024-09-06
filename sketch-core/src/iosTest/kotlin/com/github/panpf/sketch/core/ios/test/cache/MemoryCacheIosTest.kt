package com.github.panpf.sketch.core.ios.test.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.defaultMemoryCacheSizePercent
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheIosTest {

    @Test
    fun testDefaultMemoryCacheSizePercent() {
        assertEquals(
            expected = 0.20,
            actual = PlatformContext.INSTANCE.defaultMemoryCacheSizePercent(),
            absoluteTolerance = 0.0
        )
    }
}