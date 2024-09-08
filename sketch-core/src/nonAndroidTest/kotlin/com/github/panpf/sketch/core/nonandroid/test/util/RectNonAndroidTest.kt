package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.SkiaRect
import com.github.panpf.sketch.util.toSkiaRect
import kotlin.test.Test
import kotlin.test.assertEquals

class RectNonAndroidTest {

    @Test
    fun testToSkiaRect() {
        val sketchRect = SketchRect(1, 2, 3, 4)
        val skiaRect = sketchRect.toSkiaRect()
        assertEquals(SkiaRect(1f, 2f, 3f, 4f), skiaRect)
    }
}