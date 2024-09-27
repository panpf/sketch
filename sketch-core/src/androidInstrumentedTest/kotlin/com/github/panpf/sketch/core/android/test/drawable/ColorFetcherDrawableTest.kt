package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.ColorFetcherDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorFetcherDrawableTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "ColorFetcherDrawable(${IntColor(TestColor.RED).key})",
            actual = ColorFetcherDrawable(IntColor(TestColor.RED)).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        ColorFetcherDrawable(IntColor(TestColor.RED)).apply {
            assertEquals(
                expected = TestColor.RED,
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
        ColorFetcherDrawable(ResColor(com.github.panpf.sketch.test.utils.core.R.color.colorPrimary)).apply {
            assertEquals(
                expected = context.resources.getColor(com.github.panpf.sketch.test.utils.core.R.color.colorPrimary),
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ColorFetcherDrawable(IntColor(TestColor.RED))
        val element11 = ColorFetcherDrawable(IntColor(TestColor.RED))
        val element2 = ColorFetcherDrawable(IntColor(TestColor.BLUE))

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
            expected = "ColorFetcherDrawable(${IntColor(TestColor.RED)})",
            actual = ColorFetcherDrawable(IntColor(TestColor.RED)).toString()
        )
    }
}