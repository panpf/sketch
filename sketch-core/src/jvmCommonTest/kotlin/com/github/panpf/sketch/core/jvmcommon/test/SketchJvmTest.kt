package com.github.panpf.sketch.core.jvmcommon.test

import com.github.panpf.sketch.defaultHttpStack
import com.github.panpf.sketch.http.HurlStack
import kotlin.test.Test
import kotlin.test.assertEquals

class SketchJvmTest {

    @Test
    fun testDefaultHttpStack() {
        assertEquals(
            expected = true,
            actual = defaultHttpStack() is HurlStack
        )
    }
}