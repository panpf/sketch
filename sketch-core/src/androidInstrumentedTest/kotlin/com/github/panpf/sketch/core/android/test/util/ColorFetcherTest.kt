package com.github.panpf.sketch.core.android.test.util

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorFetcherTest {

    @Test
    fun testIntColorFetcher() {
        val context = getTestContext()

        IntColorFetcher(Color.RED).apply {
            assertEquals(Color.RED, color)
            assertEquals(Color.RED, getColor(context))
        }

        IntColorFetcher(Color.GREEN).apply {
            assertEquals(Color.GREEN, color)
            assertEquals(Color.GREEN, getColor(context))
        }

        val element1 = IntColorFetcher(Color.RED)
        val element11 = IntColorFetcher(Color.RED)
        val element2 = IntColorFetcher(Color.GREEN)
        val element3 = IntColorFetcher(Color.BLUE)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())

        IntColorFetcher(Color.RED).apply {
            assertEquals("IntColorFetcher(color=${Color.RED})", key)
        }
        IntColorFetcher(Color.GREEN).apply {
            assertEquals("IntColorFetcher(color=${Color.GREEN})", toString())
        }
    }

    @Test
    fun testResColorFetcher() {
        val context = getTestContext()

        ResColorFetcher(android.R.color.background_dark).apply {
            assertEquals(android.R.color.background_dark, resId)
            assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_dark, null),
                getColor(context)
            )
        }
        ResColorFetcher(android.R.color.background_light).apply {
            assertEquals(android.R.color.background_light, resId)
            assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_light, null),
                getColor(context)
            )
        }

        val element1 = ResColorFetcher(android.R.color.background_dark)
        val element11 = ResColorFetcher(android.R.color.background_dark)
        val element2 = ResColorFetcher(android.R.color.background_light)
        val element3 = ResColorFetcher(android.R.color.darker_gray)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())

        ResColorFetcher(android.R.color.background_dark).apply {
            assertEquals("ResColorFetcher(resId=${android.R.color.background_dark})", key)
        }
        ResColorFetcher(android.R.color.background_light).apply {
            assertEquals("ResColorFetcher(resId=${android.R.color.background_light})", toString())
        }
    }
}