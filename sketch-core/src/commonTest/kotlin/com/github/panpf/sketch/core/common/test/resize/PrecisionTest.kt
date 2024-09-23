package com.github.panpf.sketch.core.common.test.resize

import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.isSmallerSizeMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrecisionTest {

    @Test
    fun test() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "LESS_PIXELS, SMALLER_SIZE, SAME_ASPECT_RATIO, EXACTLY",
            actual = Precision.values().joinToString()
        )
    }

    @Test
    fun testIsSmallerSizeMode() {
        assertFalse(Precision.LESS_PIXELS.isSmallerSizeMode())
        assertTrue(Precision.SMALLER_SIZE.isSmallerSizeMode())
        assertFalse(Precision.SAME_ASPECT_RATIO.isSmallerSizeMode())
        assertFalse(Precision.EXACTLY.isSmallerSizeMode())
    }
}