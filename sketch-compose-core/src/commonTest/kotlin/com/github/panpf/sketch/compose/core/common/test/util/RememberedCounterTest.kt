package com.github.panpf.sketch.compose.core.common.test.util

import com.github.panpf.sketch.util.RememberedCounter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RememberedCounterTest {

    @Test
    fun test() {
        val rememberedCounter = RememberedCounter()
        assertEquals(expected = 0, actual = rememberedCounter.count)
        assertEquals(expected = false, actual = rememberedCounter.isRemembered)

        assertTrue(rememberedCounter.remember())
        assertEquals(expected = 1, actual = rememberedCounter.count)
        assertEquals(expected = true, actual = rememberedCounter.isRemembered)

        assertFalse(rememberedCounter.remember())
        assertEquals(expected = 2, actual = rememberedCounter.count)
        assertEquals(expected = true, actual = rememberedCounter.isRemembered)

        assertFalse(rememberedCounter.remember())
        assertEquals(expected = 3, actual = rememberedCounter.count)
        assertEquals(expected = true, actual = rememberedCounter.isRemembered)

        assertFalse(rememberedCounter.forget())
        assertEquals(expected = 2, actual = rememberedCounter.count)
        assertEquals(expected = true, actual = rememberedCounter.isRemembered)

        assertFalse(rememberedCounter.forget())
        assertEquals(expected = 1, actual = rememberedCounter.count)
        assertEquals(expected = true, actual = rememberedCounter.isRemembered)

        assertTrue(rememberedCounter.forget())
        assertEquals(expected = 0, actual = rememberedCounter.count)
        assertEquals(expected = false, actual = rememberedCounter.isRemembered)

        assertFalse(rememberedCounter.forget())
        assertEquals(expected = 0, actual = rememberedCounter.count)
        assertEquals(expected = false, actual = rememberedCounter.isRemembered)
    }
}