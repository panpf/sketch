package com.github.panpf.sketch.core.android.test.util

import com.github.panpf.sketch.util.toAndroidRect
import com.github.panpf.sketch.util.toSketchRect
import kotlin.test.Test
import kotlin.test.assertEquals

class RectAndroidTest {

    @Test
    fun testToAndroidRect() {
        assertEquals(
            expected = android.graphics.Rect::class,
            actual = com.github.panpf.sketch.util.Rect(1, 2, 3, 4).toAndroidRect()::class
        )
    }

    @Test
    fun testToSketchRect() {
        assertEquals(
            expected = com.github.panpf.sketch.util.Rect::class,
            actual = android.graphics.Rect(1, 2, 3, 4).toSketchRect()::class
        )
    }
}