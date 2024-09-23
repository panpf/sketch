package com.github.panpf.sketch.core.common.test.resize

import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.reverse
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleTest {

    @Test
    fun test() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "START_CROP, CENTER_CROP, END_CROP, FILL",
            actual = Scale.values().joinToString()
        )
    }

    @Test
    fun testReverse() {
        assertEquals(Scale.START_CROP, Scale.END_CROP.reverse())
        assertEquals(Scale.CENTER_CROP, Scale.CENTER_CROP.reverse())
        assertEquals(Scale.END_CROP, Scale.START_CROP.reverse())
        assertEquals(Scale.FILL, Scale.FILL.reverse())
    }
}