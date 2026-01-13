package com.github.panpf.sketch.core.jscommon.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.platformComponents
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class SketchJsCommonTest {

    @Test
    fun testPlatformComponents() {
        val context = getTestContext()
        assertEquals(
            expected = ComponentRegistry {
                add(SkiaDecoder.Factory())
            },
            actual = platformComponents(context)
        )
    }
}