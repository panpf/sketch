package com.github.panpf.sketch.core.android.test.util

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import org.junit.Assert
import kotlin.test.Test

class ColorFetcherTest {

    @Test
    fun testIntColor() {
        val context = getTestContext()

        IntColor(Color.RED).apply {
            Assert.assertEquals(Color.RED, color)
            Assert.assertEquals(Color.RED, getColor(context))
        }

        IntColor(Color.GREEN).apply {
            Assert.assertEquals(Color.GREEN, color)
            Assert.assertEquals(Color.GREEN, getColor(context))
        }

        val element1 = IntColor(Color.RED)
        val element11 = IntColor(Color.RED)
        val element2 = IntColor(Color.GREEN)
        val element3 = IntColor(Color.BLUE)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())

        IntColor(Color.RED).apply {
            Assert.assertEquals("IntColor(${Color.RED})", toString())
        }
        IntColor(Color.GREEN).apply {
            Assert.assertEquals("IntColor(${Color.GREEN})", toString())
        }
    }

    @Test
    fun testResColor() {
        val context = getTestContext()

        ResColor(android.R.color.background_dark).apply {
            Assert.assertEquals(android.R.color.background_dark, resId)
            Assert.assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_dark, null),
                getColor(context)
            )
        }
        ResColor(android.R.color.background_light).apply {
            Assert.assertEquals(android.R.color.background_light, resId)
            Assert.assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_light, null),
                getColor(context)
            )
        }

        val element1 = ResColor(android.R.color.background_dark)
        val element11 = ResColor(android.R.color.background_dark)
        val element2 = ResColor(android.R.color.background_light)
        val element3 = ResColor(android.R.color.darker_gray)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())

        ResColor(android.R.color.background_dark).apply {
            Assert.assertEquals("ResColor(${android.R.color.background_dark})", toString())
        }
        ResColor(android.R.color.background_light).apply {
            Assert.assertEquals("ResColor(${android.R.color.background_light})", toString())
        }
    }
}