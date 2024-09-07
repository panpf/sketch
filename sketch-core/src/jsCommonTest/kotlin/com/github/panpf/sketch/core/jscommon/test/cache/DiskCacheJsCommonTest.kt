package com.github.panpf.sketch.core.jscommon.test.cache

import com.github.panpf.sketch.cache.defaultDiskCacheMaxSize
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class DiskCacheJsCommonTest {

    @Test
    fun testDefaultDiskCacheMaxSize() {
        val context = getTestContext()
        assertEquals(
            expected = null,
            actual = defaultDiskCacheMaxSize(context)
        )
    }
}