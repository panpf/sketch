package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.RealColorDrawableFetcher
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RealColorDrawableFetcherTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "RealColorDrawableFetcher(color=${TestColor.RED})",
            actual = RealColorDrawableFetcher(TestColor.RED).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        RealColorDrawableFetcher(TestColor.RED).apply {
            assertEquals(
                expected = TestColor.RED,
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = RealColorDrawableFetcher(TestColor.RED)
        val element11 = RealColorDrawableFetcher(TestColor.RED)
        val element2 = RealColorDrawableFetcher(TestColor.BLUE)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "RealColorDrawableFetcher(color=${TestColor.RED})",
            actual = RealColorDrawableFetcher(TestColor.RED).toString()
        )
    }
}