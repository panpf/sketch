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
    fun testEquals() {
        val stateImage1 = ColorStateImage(Color.RED)
        val stateImage11 = ColorStateImage(Color.RED)

        val stateImage2 = ColorStateImage(Color.GREEN)
        val stateImage21 = ColorStateImage(Color.GREEN)

        val stateImage3 = ColorStateImage(Color.BLUE)
        val stateImage31 = ColorStateImage(Color.BLUE)

        Assert.assertNotSame(stateImage1, stateImage11)
        Assert.assertNotSame(stateImage2, stateImage21)
        Assert.assertNotSame(stateImage3, stateImage31)

        Assert.assertEquals(stateImage1, stateImage11)
        Assert.assertEquals(stateImage2, stateImage21)
        Assert.assertEquals(stateImage3, stateImage31)

        Assert.assertNotEquals(stateImage1, stateImage2)
        Assert.assertNotEquals(stateImage1, stateImage3)
        Assert.assertNotEquals(stateImage2, stateImage3)
    }

    @Test
    fun testHashCode() {
        val stateImage1 = ColorStateImage(Color.RED)
        val stateImage11 = ColorStateImage(Color.RED)

        val stateImage2 = ColorStateImage(Color.GREEN)
        val stateImage21 = ColorStateImage(Color.GREEN)

        val stateImage3 = ColorStateImage(Color.BLUE)
        val stateImage31 = ColorStateImage(Color.BLUE)

        Assert.assertEquals(stateImage1.hashCode(), stateImage11.hashCode())
        Assert.assertEquals(stateImage2.hashCode(), stateImage21.hashCode())
        Assert.assertEquals(stateImage3.hashCode(), stateImage31.hashCode())

        Assert.assertNotEquals(stateImage1.hashCode(), stateImage2.hashCode())
        Assert.assertNotEquals(stateImage1.hashCode(), stateImage3.hashCode())
        Assert.assertNotEquals(stateImage2.hashCode(), stateImage3.hashCode())
    }

    @Test
    fun testToString() {
        ColorStateImage(Color.RED).apply {
            Assert.assertEquals("ColorStateImage(color=IntColor(${Color.RED}))", toString())
        }
        ColorStateImage(Color.GREEN).apply {
            Assert.assertEquals("ColorStateImage(color=IntColor(${Color.GREEN}))", toString())
        }
    }
}