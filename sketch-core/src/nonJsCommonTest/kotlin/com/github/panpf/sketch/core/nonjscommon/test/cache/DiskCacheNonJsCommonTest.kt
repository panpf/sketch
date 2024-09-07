package com.github.panpf.sketch.core.nonjscommon.test.cache

import com.github.panpf.sketch.cache.defaultDiskCacheMaxSize
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class DiskCacheNonJsCommonTest {

    @Test
    fun testDefaultDiskCacheMaxSize() {
        val context = getTestContext()
        assertEquals(
            expected = 512L * 1024 * 1024,
            actual = defaultDiskCacheMaxSize(context)
        )
    }
}