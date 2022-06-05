package com.github.panpf.sketch.test.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrossfadeDrawableTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val resources = context.resources
        val startDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
        val endDrawable = BitmapDrawable(resources, Bitmap.createBitmap(200, 100, RGB_565))

        CrossfadeDrawable(startDrawable, endDrawable).apply {
            Assert.assertTrue(fitScale)
            Assert.assertEquals(100, durationMillis)
            Assert.assertTrue(fadeStart)
            Assert.assertFalse(preferExactIntrinsicSize)
        }
        CrossfadeDrawable(
            startDrawable,
            endDrawable,
            fitScale = false,
            durationMillis = 2000,
            fadeStart = false,
            preferExactIntrinsicSize = true
        ).apply {
            Assert.assertFalse(fitScale)
            Assert.assertEquals(2000, durationMillis)
            Assert.assertFalse(fadeStart)
            Assert.assertTrue(preferExactIntrinsicSize)
        }
    }

    @Test
    fun testSize() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val resources = context.resources
        val startDrawable =
            BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565)).apply {
                Assert.assertEquals(Size(100, 200), intrinsicSize)
            }
        val endDrawable = BitmapDrawable(resources, Bitmap.createBitmap(200, 100, RGB_565)).apply {
            Assert.assertEquals(Size(200, 100), intrinsicSize)
        }
        CrossfadeDrawable(startDrawable, endDrawable).apply {
            Assert.assertEquals(Size(200, 200), intrinsicSize)
        }
    }

    @Test
    fun testBounds() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val resources = context.resources

        val startDrawable =
            BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565)).apply {
                Assert.assertEquals(Rect(), bounds)
            }
        val endDrawable = BitmapDrawable(resources, Bitmap.createBitmap(200, 100, RGB_565)).apply {
            Assert.assertEquals(Rect(), bounds)
        }
        val crossfadeDrawable = CrossfadeDrawable(startDrawable, endDrawable).apply {
            Assert.assertEquals(Rect(), bounds)
        }

        crossfadeDrawable.setBounds(0, 0, 200, 200)
        Assert.assertEquals(Rect(50, 0, 150, 200), startDrawable.bounds)
        Assert.assertEquals(Rect(0, 50, 200, 150), endDrawable.bounds)
        Assert.assertEquals(Rect(0, 0, 200, 200), crossfadeDrawable.bounds)
    }
}