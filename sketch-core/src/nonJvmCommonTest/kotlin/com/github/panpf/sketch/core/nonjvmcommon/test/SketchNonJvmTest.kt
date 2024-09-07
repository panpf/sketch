package com.github.panpf.sketch.core.nonjvmcommon.test

import com.github.panpf.sketch.defaultHttpStack
import com.github.panpf.sketch.http.KtorStack
import kotlin.test.Test
import kotlin.test.assertEquals

class SketchNonJvmTest {

    @Test
    fun testDefaultHttpStack() {
        assertEquals(
            expected = true,
            actual = defaultHttpStack() is KtorStack
        )
    }
}