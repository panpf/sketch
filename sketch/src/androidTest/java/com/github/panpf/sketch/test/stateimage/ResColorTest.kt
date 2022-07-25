package com.github.panpf.sketch.test.stateimage

import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResColorTest {

    @Test
    fun testGetColor() {
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
    }

    @Test
    fun testToString() {
        ResColor(android.R.color.background_dark).apply {
            Assert.assertEquals("ResColor(${android.R.color.background_dark})", toString())
        }

        ResColor(android.R.color.background_light).apply {
            Assert.assertEquals("ResColor(${android.R.color.background_light})", toString())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
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
    }
}