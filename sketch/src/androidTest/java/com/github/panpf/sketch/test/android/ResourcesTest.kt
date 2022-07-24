package com.github.panpf.sketch.test.android

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.alphaCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.getDrawableCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourcesTest {

    @Test
    fun testBitmapDrawableBitmap() {
        val context = getTestContext()

        val drawable1 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        val drawable2 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable

        Assert.assertNotSame(drawable1, drawable2)
        Assert.assertSame(drawable1.bitmap, drawable2.bitmap)

        drawable2.bitmap.recycle()
        Assert.assertTrue(drawable1.bitmap.isRecycled)
        val drawable3 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        Assert.assertTrue(drawable3.bitmap.isRecycled)
    }

    @Test
    fun testBitmapDrawableMutate() {
        val context = getTestContext()

        val drawable1 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        val drawable2 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable

        Assert.assertNotSame(drawable1, drawable2)
        Assert.assertSame(drawable1.paint, drawable2.paint)
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(255, drawable1.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable1.alphaCompat)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(255, drawable2.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable2.alphaCompat)
        }

        val drawable3 = drawable1.mutate() as BitmapDrawable
        Assert.assertSame(drawable1, drawable3)
        Assert.assertSame(drawable1.paint, drawable3.paint)
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(255, drawable3.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable3.alphaCompat)
        }

        drawable3.alpha = 100
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(100, drawable1.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable1.alphaCompat)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(255, drawable2.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable2.alphaCompat)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(100, drawable3.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable3.alphaCompat)
        }

        val drawable4 = context.getDrawableCompat(android.R.drawable.ic_delete) as BitmapDrawable
        if (Build.VERSION.SDK_INT >= 19) {
            Assert.assertEquals(255, drawable4.alphaCompat)
        } else {
            Assert.assertEquals(0, drawable4.alphaCompat)
        }
    }
}