package com.github.panpf.sketch.core.android.test.util

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class ColorFetcherTest {

    @Test
    fun testIntColor() {
        val context = getTestContext()

        IntColor(Color.RED).apply {
            assertEquals(Color.RED, color)
            assertEquals(Color.RED, getColor(context))
        }

        IntColor(Color.GREEN).apply {
            assertEquals(Color.GREEN, color)
            assertEquals(Color.GREEN, getColor(context))
        }

        val element1 = IntColor(Color.RED)
        val element11 = IntColor(Color.RED)
        val element2 = IntColor(Color.GREEN)
        val element3 = IntColor(Color.BLUE)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())

        IntColor(Color.RED).apply {
            assertEquals("IntColor(${Color.RED})", toString())
        }
        IntColor(Color.GREEN).apply {
            assertEquals("IntColor(${Color.GREEN})", toString())
        }
    }

    @Test
    fun testResColor() {
        val context = getTestContext()

        ResColor(android.R.color.background_dark).apply {
            assertEquals(android.R.color.background_dark, resId)
            assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_dark, null),
                getColor(context)
            )
        }
        ResColor(android.R.color.background_light).apply {
            assertEquals(android.R.color.background_light, resId)
            assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_light, null),
                getColor(context)
            )
        }

        val element1 = ResColor(android.R.color.background_dark)
        val element11 = ResColor(android.R.color.background_dark)
        val element2 = ResColor(android.R.color.background_light)
        val element3 = ResColor(android.R.color.darker_gray)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())

        ResColor(android.R.color.background_dark).apply {
            assertEquals("ResColor(${android.R.color.background_dark})", toString())
        }
        ResColor(android.R.color.background_light).apply {
            assertEquals("ResColor(${android.R.color.background_light})", toString())
        }
    }
}