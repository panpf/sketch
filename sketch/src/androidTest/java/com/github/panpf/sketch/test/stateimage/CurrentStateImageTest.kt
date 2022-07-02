package com.github.panpf.sketch.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.stateimage.CurrentStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrentStateImageTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))

        CurrentStateImage().apply {
            Assert.assertNull(getDrawable(sketch, request, null))
        }

        CurrentStateImage(android.R.drawable.btn_default).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is StateListDrawable)
        }

        CurrentStateImage(ColorDrawable(Color.RED)).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is ColorDrawable)
        }
    }

    @Test
    fun testGetDrawable() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))
        val drawable1 = ColorDrawable(Color.BLUE)
        val drawable2 = ColorDrawable(Color.GREEN)

        Assert.assertNull(imageView.drawable)
        CurrentStateImage().apply {
            Assert.assertNull(getDrawable(sketch, request, null))
            imageView.setImageDrawable(drawable1)
            Assert.assertSame(drawable1, getDrawable(sketch, request, null))
        }

        imageView.setImageDrawable(null)
        Assert.assertNull(imageView.drawable)
        CurrentStateImage(DrawableStateImage(drawable2)).apply {
            Assert.assertSame(drawable2, getDrawable(sketch, request, null))
            imageView.setImageDrawable(drawable1)
            Assert.assertSame(drawable1, getDrawable(sketch, request, null))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = CurrentStateImage(android.R.drawable.btn_default)
        val element11 = CurrentStateImage(android.R.drawable.btn_default)
        val element2 = CurrentStateImage(android.R.drawable.btn_dialog)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        CurrentStateImage().apply {
            Assert.assertEquals(
                "CurrentStateImage(defaultImage=null)",
                toString()
            )
        }
        CurrentStateImage(android.R.drawable.btn_default).apply {
            Assert.assertEquals(
                "CurrentStateImage(defaultImage=DrawableStateImage(drawable=ResDrawable(${android.R.drawable.btn_default})))",
                toString()
            )
        }
    }
}