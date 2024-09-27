package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.RealEqualityDrawable
import com.github.panpf.sketch.drawable.asEquality
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RealEqualityDrawableTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "RealEqualityDrawable(${ColorDrawable(TestColor.RED).asEquality().key})",
            actual = RealEqualityDrawable(ColorDrawable(TestColor.RED).asEquality()).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        RealEqualityDrawable(ColorDrawable(TestColor.RED).asEquality()).apply {
            assertEquals(
                expected = TestColor.RED,
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = RealEqualityDrawable(ColorDrawable(TestColor.RED).asEquality())
        val element11 = RealEqualityDrawable(ColorDrawable(TestColor.RED).asEquality())
        val element2 = RealEqualityDrawable(ColorDrawable(TestColor.BLUE).asEquality())

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
            expected = "RealEqualityDrawable(${ColorDrawable(TestColor.RED).asEquality()})",
            actual = RealEqualityDrawable(ColorDrawable(TestColor.RED).asEquality()).toString()
        )
    }
}