package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.RealDrawableFetcher
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RealDrawableFetcherTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "RealDrawableFetcher(drawable=${ColorDrawable(TestColor.RED).asEquitable().key})",
            actual = RealDrawableFetcher(ColorDrawable(TestColor.RED).asEquitable()).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        RealDrawableFetcher(ColorDrawable(TestColor.RED).asEquitable()).apply {
            assertEquals(
                expected = TestColor.RED,
                actual = getDrawable(context)
                    .asOrThrow<EquitableDrawable>().drawable
                    .asOrThrow<ColorDrawable>().color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = RealDrawableFetcher(ColorDrawable(TestColor.RED).asEquitable())
        val element11 = RealDrawableFetcher(ColorDrawable(TestColor.RED).asEquitable())
        val element2 = RealDrawableFetcher(ColorDrawable(TestColor.BLUE).asEquitable())

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
            expected = "RealDrawableFetcher(drawable=${ColorDrawable(TestColor.RED).asEquitable()})",
            actual = RealDrawableFetcher(ColorDrawable(TestColor.RED).asEquitable()).toString()
        )
    }
}