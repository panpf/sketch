package com.github.panpf.sketch.extensions.compose.common.test.util

import com.github.panpf.sketch.extensions.compose.internal.util.format
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsComposeInternalUtilsTest {

    @Test
    fun testFormat() {
        assertEquals(1.412f, 1.412412f.format(3))
        assertEquals(1.41f, 1.412412f.format(2))
        assertEquals(1.4f, 1.412412f.format(1))
        assertEquals(1f, 1.412412f.format(0))
    }
}