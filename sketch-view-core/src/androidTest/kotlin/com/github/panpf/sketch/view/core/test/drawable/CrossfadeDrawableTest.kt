/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.view.core.test.drawable

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.BlendMode.CLEAR
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.DST
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CrossfadeDrawableTest {

    @Test
    fun testConstructor() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val resources = context.resources
        val startDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
        val endDrawable = BitmapDrawable(resources, Bitmap.createBitmap(200, 100, RGB_565))

        CrossfadeDrawable(startDrawable, endDrawable).apply {
            assertTrue(fitScale)
            assertEquals(200, durationMillis)
            assertTrue(fadeStart)
            assertFalse(preferExactIntrinsicSize)
        }
        CrossfadeDrawable(
            startDrawable,
            endDrawable,
            fitScale = false,
            durationMillis = 2000,
            fadeStart = false,
            preferExactIntrinsicSize = true
        ).apply {
            assertFalse(fitScale)
            assertEquals(2000, durationMillis)
            assertFalse(fadeStart)
            assertTrue(preferExactIntrinsicSize)
        }
    }

    @Test
    fun testSize() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val resources = context.resources
        val startDrawable =
            BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565)).apply {
                assertEquals(Size(100, 200), intrinsicSize)
            }
        val endDrawable = BitmapDrawable(resources, Bitmap.createBitmap(200, 100, RGB_565)).apply {
            assertEquals(Size(200, 100), intrinsicSize)
        }
        CrossfadeDrawable(startDrawable, endDrawable).apply {
            assertEquals(Size(200, 200), intrinsicSize)
        }
    }

    @Test
    fun testBounds() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val resources = context.resources

        val startDrawable =
            BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565)).apply {
                assertEquals(Rect(), bounds)
            }
        val endDrawable = BitmapDrawable(resources, Bitmap.createBitmap(200, 100, RGB_565)).apply {
            assertEquals(Rect(), bounds)
        }
        val crossfadeDrawable = CrossfadeDrawable(startDrawable, endDrawable).apply {
            assertEquals(Rect(), bounds)
        }

        crossfadeDrawable.setBounds(0, 0, 200, 200)
        assertEquals(Rect(50, 0, 150, 200), startDrawable.bounds)
        assertEquals(Rect(0, 50, 200, 150), endDrawable.bounds)
        assertEquals(Rect(0, 0, 200, 200), crossfadeDrawable.bounds)
    }

    @Test
    fun testCallback() {
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
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
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_input_add).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
            context.getDrawableCompat(android.R.drawable.ic_delete).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        CrossfadeDrawable(
            TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.ic_input_add)),
            TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.ic_delete)),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_input_add).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
            context.getDrawableCompat(android.R.drawable.ic_delete).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testTint() {
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
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
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            val callbackAction = mutableListOf<String>()
            val callback3 = object : Animatable2Compat.AnimationCallback() {
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

            assertFalse(isRunning)
            assertEquals(listOf<String>(), callbackAction)

            start()
            Thread.sleep(100)
            assertTrue(isRunning)
            assertEquals(listOf("onAnimationStart"), callbackAction)

            stop()
            Thread.sleep(100)
            assertFalse(isRunning)
            assertEquals(listOf("onAnimationStart", "onAnimationEnd"), callbackAction)
        }
    }

    @Test
    fun testColorFilter() {
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            if (Build.VERSION.SDK_INT >= 21) {
                assertNull(colorFilter)
            }
            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (Build.VERSION.SDK_INT >= 21) {
                assertTrue(colorFilter is PorterDuffColorFilter)

                start()
                assertTrue(colorFilter is PorterDuffColorFilter)

                stop()
                assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }
    }

    @Test
    fun testChange() {
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }
    }

    @Test
    fun testOpacity() {
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            assertEquals(PixelFormat.TRANSLUCENT, opacity)

            start()
            assertEquals(PixelFormat.TRANSLUCENT, opacity)

            stop()
            assertEquals(PixelFormat.TRANSLUCENT, opacity)
        }
    }

    @Test
    fun testDraw() {
        val context = getTestContext()

        CrossfadeDrawable(
            context.getDrawableCompat(android.R.drawable.ic_input_add),
            context.getDrawableCompat(android.R.drawable.ic_delete),
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
}