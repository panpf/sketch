package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.application
import kotlin.test.Test
import kotlin.test.assertSame

class PlatformContextsNonAndroidTest {

    @Test
    fun testApplication() {
        val context = getTestContext()
        assertSame(context, context.application)
    }
}