package com.github.panpf.sketch.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.util.asOrNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))

        ColorStateImage(Color.BLUE).apply {
            Assert.assertEquals(
                Color.BLUE,
                getDrawable(sketch, request, null).asOrNull<ColorDrawable>()!!.color
            )
        }

        ColorStateImage(IntColor(Color.RED)).apply {
            Assert.assertEquals(
                Color.RED,
                getDrawable(sketch, request, null).asOrNull<ColorDrawable>()!!.color
            )
        }

        ColorStateImage(IntColor(Color.GREEN)).apply {
            Assert.assertEquals(
                Color.GREEN,
                getDrawable(sketch, request, null).asOrNull<ColorDrawable>()!!.color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ColorStateImage(Color.RED)
        val element11 = ColorStateImage(Color.RED)
        val element2 = ColorStateImage(Color.GREEN)
        val element3 = ColorStateImage(Color.BLUE)

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

    @Test
    fun testToString() {
        ColorStateImage(Color.RED).apply {
            Assert.assertEquals("ColorStateImage(IntColor(${Color.RED}))", toString())
        }
        ColorStateImage(Color.GREEN).apply {
            Assert.assertEquals("ColorStateImage(IntColor(${Color.GREEN}))", toString())
        }
    }
}