package com.github.panpf.sketch.test.drawable

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BlendMode.CLEAR
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.DST
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.drawable.internal.ScaledAnimatedImageDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScaledAnimatedImageDrawableTest {

    @Test
    fun testConstructor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            Assert.assertTrue(fitScale)
        }

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable,
            fitScale = false
        ).apply {
            Assert.assertFalse(fitScale)
        }
    }

    @Test
    fun testCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            val callback = object : Animatable2.AnimationCallback() {}
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            unregisterAnimationCallback(callback)

            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }
    }

    @Test
    fun testMutate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            mutate()
        }
    }

    @Test
    fun testTint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            setTint(Color.RED)
            setTintList(ColorStateList.valueOf(Color.GREEN))
            setTintMode(DST)
            if (Build.VERSION.SDK_INT >= 29) {
                setTintBlendMode(CLEAR)
            }
        }
    }

    @Test
    fun testStartStopIsRunning() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            val callbackAction = mutableListOf<String>()
            val callback3 = object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackAction.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackAction.add("onAnimationEnd")
                }
            }
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback3)
            }

            Assert.assertFalse(isRunning)
            Assert.assertEquals(listOf<String>(), callbackAction)

            start()
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)
            Thread.sleep(100)
            Assert.assertTrue(isRunning)
            Assert.assertEquals(listOf("onAnimationStart"), callbackAction)

            stop()
            Thread.sleep(100)
            Assert.assertFalse(isRunning)
            Assert.assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
        }
    }

    @Test
    fun testColorFilter() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertNull(colorFilter)
            }
            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }
    }

    @Test
    fun testChange() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }
    }

    @Test
    fun testOpacity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            Assert.assertEquals(PixelFormat.TRANSLUCENT, opacity)

            start()
            Assert.assertEquals(PixelFormat.TRANSLUCENT, opacity)

            stop()
            Assert.assertEquals(PixelFormat.TRANSLUCENT, opacity)
        }
    }

    @Test
    fun testDraw() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)

            start()
            draw(canvas)

            stop()
            draw(canvas)
            draw(canvas)
        }
    }

    @Test
    fun testBounds() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = InstrumentationRegistry.getInstrumentation().context

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            Assert.assertEquals(Rect(0, 0, 0, 0), bounds)
            setBounds(0, 0, 100, 200)
            Assert.assertEquals(Rect(0, 0, 100, 200), bounds)
        }
    }

    @Test
    fun testAlpha() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = InstrumentationRegistry.getInstrumentation().context

        ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, "sample_anim.gif")
            ) as AnimatedImageDrawable
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Assert.assertEquals(255, alpha)
            }

            mutate()
            alpha = 144
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Assert.assertEquals(144, alpha)
            }
        }
    }
}