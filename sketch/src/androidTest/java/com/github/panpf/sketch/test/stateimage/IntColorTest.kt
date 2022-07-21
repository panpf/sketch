package com.github.panpf.sketch.test.stateimage

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntColorTest {

    @Test
    fun testGetColor() {
        val context = getTestContext()

        IntColor(Color.RED).apply {
            Assert.assertEquals(Color.RED, colorInt)
            Assert.assertEquals(Color.RED, getColor(context))
        }

        IntColor(Color.GREEN).apply {
            Assert.assertEquals(Color.GREEN, colorInt)
            Assert.assertEquals(Color.GREEN, getColor(context))
        }
    }

    @Test
    fun testToString() {
        IntColor(Color.RED).apply {
            Assert.assertEquals("IntColor(${Color.RED})", toString())
        }

        IntColor(Color.GREEN).apply {
            Assert.assertEquals("IntColor(${Color.GREEN})", toString())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
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
    }
}