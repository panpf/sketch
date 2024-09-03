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

package com.github.panpf.sketch.animated.android.test.drawable

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
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ScaledAnimatedImageDrawableTest {

    @Test
    fun testConstructor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            assertTrue(fitScale)
        }

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable,
            fitScale = false
        ).apply {
            assertFalse(fitScale)
        }
    }

    @Test
    fun testCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            val callback = object : Animatable2.AnimationCallback() {}
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            runBlocking(Dispatchers.Main) {
                unregisterAnimationCallback(callback)
            }

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

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            mutate()
        }
    }

    @Test
    fun testTint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
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

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
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

            assertFalse(isRunning)
            assertEquals(listOf<String>(), callbackAction)

            start()
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            if (Build.VERSION.SDK_INT >= 21) {
                assertNull(colorFilter)
            }
            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (Build.VERSION.SDK_INT >= 21) {
                assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }
    }

    @Test
    fun testChange() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testOpacity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = getTestContext()

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
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

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            assertEquals(Rect(0, 0, 0, 0), bounds)
            setBounds(0, 0, 100, 200)
            assertEquals(Rect(0, 0, 100, 200), bounds)
        }
    }

    @Test
    fun testAlpha() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val context = InstrumentationRegistry.getInstrumentation().context

        com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(context.assets, ResourceImages.animGif.resourceName)
            ) as AnimatedImageDrawable
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                assertEquals(255, alpha)
            }

            mutate()
            alpha = 144
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                assertEquals(144, alpha)
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val animatedImageDrawable = ImageDecoder.decodeDrawable(
            ImageDecoder.createSource(
                context.assets,
                ResourceImages.animGif.resourceName
            )
        ) as AnimatedImageDrawable
        val animatedImageDrawable2 = ImageDecoder.decodeDrawable(
            ImageDecoder.createSource(
                context.assets,
                ResourceImages.animGif.resourceName
            )
        ) as AnimatedImageDrawable
        val element1 = com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            animatedImageDrawable,
            true
        )
        val element11 = com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            animatedImageDrawable,
            true
        )
        val element2 = com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            animatedImageDrawable2,
            true
        )
        val element3 = com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(
            animatedImageDrawable,
            false
        )

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val animatedImageDrawable = ImageDecoder.decodeDrawable(
            ImageDecoder.createSource(
                context.assets,
                ResourceImages.animGif.resourceName
            )
        ) as AnimatedImageDrawable
        assertEquals(
            "ScaledAnimatedImageDrawable(drawable=AnimatedImageDrawable(480x480), fitScale=true)",
            com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable(animatedImageDrawable)
                .toString()
        )
    }
}