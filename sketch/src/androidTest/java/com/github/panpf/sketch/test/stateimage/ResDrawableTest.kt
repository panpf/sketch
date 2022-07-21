package com.github.panpf.sketch.test.stateimage

import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.stateimage.ResDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResDrawableTest {

    @Test
    fun testGetDrawable() {
        val context = getTestContext()

        ResDrawable(android.R.drawable.ic_delete).apply {
            Assert.assertEquals(android.R.drawable.ic_delete, drawableRes)
            Assert.assertSame(
                ResourcesCompat.getDrawable(context.resources, android.R.drawable.ic_delete, null)!!
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }

        ResDrawable(android.R.drawable.bottom_bar).apply {
            Assert.assertEquals(android.R.drawable.bottom_bar, drawableRes)
            Assert.assertSame(
                ResourcesCompat.getDrawable(context.resources, android.R.drawable.bottom_bar, null)!!
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }
    }

    @Test
    fun testToString() {
        ResDrawable(android.R.drawable.ic_delete).apply {
            Assert.assertEquals("ResDrawable(${android.R.drawable.ic_delete})", toString())
        }

        ResDrawable(android.R.drawable.bottom_bar).apply {
            Assert.assertEquals("ResDrawable(${android.R.drawable.bottom_bar})", toString())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResDrawable(android.R.drawable.ic_delete)
        val element11 = ResDrawable(android.R.drawable.ic_delete)
        val element2 = ResDrawable(android.R.drawable.bottom_bar)
        val element3 = ResDrawable(android.R.drawable.btn_dialog)

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