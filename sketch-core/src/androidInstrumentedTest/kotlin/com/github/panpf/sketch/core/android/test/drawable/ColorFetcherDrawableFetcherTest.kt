package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.ColorFetcherDrawableFetcher
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorFetcherDrawableFetcherTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "ColorFetcherDrawableFetcher(color=${IntColorFetcher(TestColor.RED).key})",
            actual = ColorFetcherDrawableFetcher(IntColorFetcher(TestColor.RED)).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        ColorFetcherDrawableFetcher(IntColorFetcher(TestColor.RED)).apply {
            assertEquals(
                expected = TestColor.RED,
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
        ColorFetcherDrawableFetcher(ResColorFetcher(com.github.panpf.sketch.test.utils.core.R.color.colorPrimary)).apply {
            assertEquals(
                expected = context.resources.getColor(com.github.panpf.sketch.test.utils.core.R.color.colorPrimary),
                actual = getDrawable(context).asOrThrow<ColorDrawable>().color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ColorFetcherDrawableFetcher(IntColorFetcher(TestColor.RED))
        val element11 = ColorFetcherDrawableFetcher(IntColorFetcher(TestColor.RED))
        val element2 = ColorFetcherDrawableFetcher(IntColorFetcher(TestColor.BLUE))

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
            expected = "ColorFetcherDrawableFetcher(color=${IntColorFetcher(TestColor.RED)})",
            actual = ColorFetcherDrawableFetcher(IntColorFetcher(TestColor.RED)).toString()
        )
    }
}