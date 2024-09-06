package com.github.panpf.sketch.core.jscommon.test.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.defaultMemoryCacheSizePercent
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryCacheJsCommonTest {

    @Test
    fun testDefaultMemoryCacheSizePercent() {
        assertEquals(
            expected = 0.20,
            actual = PlatformContext.INSTANCE.defaultMemoryCacheSizePercent(),
            absoluteTolerance = 0.0
        )
    }
}