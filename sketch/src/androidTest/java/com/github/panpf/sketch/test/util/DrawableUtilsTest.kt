package com.github.panpf.sketch.test.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getLastChildDrawable
import com.github.panpf.sketch.util.toNewBitmap
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableUtilsTest {

    @Test
    fun testGetLastChildDrawable() {
        LayerDrawable(
            arrayOf(
                ColorDrawable(Color.BLUE),
                ColorDrawable(Color.RED),
                ColorDrawable(Color.GREEN)
            )
        ).getLastChildDrawable().apply {
            Assert.assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        LayerDrawable(
            arrayOf(
                ColorDrawable(Color.RED),
                ColorDrawable(Color.GREEN),
                ColorDrawable(Color.BLUE),
            )
        ).getLastChildDrawable().apply {
            Assert.assertEquals(Color.BLUE, this!!.asOrThrow<ColorDrawable>().color)
        }

        LayerDrawable(arrayOf()).getLastChildDrawable().apply {
            Assert.assertEquals(null, this)
        }
    }

    @Test
    fun testToNewBitmap() {
        val context = getTestContext()
        val bitmapPool = LruBitmapPool(1024 * 1024 * 100)

        val drawable = BitmapDrawable(
            context.resources,
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        )

        Assert.assertEquals(Rect(0, 0, 0, 0), drawable.bounds)
        drawable.toNewBitmap(bitmapPool, null).apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, config)
            Assert.assertEquals("Bitmap(100x100,ARGB_8888)", toShortInfoString())
        }
        Assert.assertEquals(Rect(0, 0, 0, 0), drawable.bounds)

        drawable.setBounds(100, 100, 200, 200)
        Assert.assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
        drawable.toNewBitmap(bitmapPool, Bitmap.Config.RGB_565).apply {
            Assert.assertEquals(Bitmap.Config.RGB_565, config)
            Assert.assertEquals("Bitmap(100x100,RGB_565)", toShortInfoString())
        }
        Assert.assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
    }
}