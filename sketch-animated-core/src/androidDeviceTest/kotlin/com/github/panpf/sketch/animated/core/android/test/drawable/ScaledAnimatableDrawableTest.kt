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

package com.github.panpf.sketch.animated.core.android.test.drawable

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
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.ScaledAnimatableDrawable
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2Drawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toLogString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.buffer
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ScaledAnimatableDrawableTest {

    @Test
    fun testConstructor() = runTest {
        val context = getTestContext()

        assertFailsWith(IllegalArgumentException::class) {
            ScaledAnimatableDrawable(ColorDrawable(Color.BLUE))
        }

        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            assertTrue(fitScale)
        }

        ScaledAnimatableDrawable(
            TestAnimatable2CompatDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            assertTrue(fitScale)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ScaledAnimatableDrawable(
                TestAnimatable2Drawable(ColorDrawable(Color.BLUE))
            ).apply {
                assertTrue(fitScale)
            }

            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
                .let { ByteBuffer.wrap(it) }
                .let { ImageDecoder.createSource(it) }
                .let { ImageDecoder.decodeDrawable(it) }
                .let { ScaledAnimatableDrawable(it) }
                .apply {
                    assertTrue(fitScale)
                }
        }
    }

    @Test
    fun testCallback() = runTest {
        val context = getTestContext()
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            withContext(Dispatchers.Main) {
                unregisterAnimationCallback(callback)
            }
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }

        ScaledAnimatableDrawable(
            TestAnimatable2CompatDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            val callback = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            withContext(Dispatchers.Main) {
                unregisterAnimationCallback(callback)
            }
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            clearAnimationCallbacks()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ScaledAnimatableDrawable(
                TestAnimatable2Drawable(ColorDrawable(Color.BLUE))
            ).apply {
                val callback = object : Animatable2Compat.AnimationCallback() {}
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(callback)
                }
                withContext(Dispatchers.Main) {
                    unregisterAnimationCallback(callback)
                }
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(callback)
                }
                clearAnimationCallbacks()
            }

            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
                .let { ByteBuffer.wrap(it) }
                .let { ImageDecoder.createSource(it) }
                .let { ImageDecoder.decodeDrawable(it) }
                .let { it as AnimatedImageDrawable }
                .let { ScaledAnimatableDrawable(it) }
                .apply {
                    val callback = object : Animatable2Compat.AnimationCallback() {}
                    withContext(Dispatchers.Main) {
                        registerAnimationCallback(callback)
                    }
                    withContext(Dispatchers.Main) {
                        unregisterAnimationCallback(callback)
                    }
                    withContext(Dispatchers.Main) {
                        registerAnimationCallback(callback)
                    }
                    clearAnimationCallbacks()
                }
        }
    }

    @Test
    fun testMutate() {
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            mutate()
        }
    }

    @Test
    fun testTint() {
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
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
    fun testStartStopIsRunning() = runTest {
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
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
            withContext(Dispatchers.Main) {
                registerAnimationCallback(callback3)
            }

            assertFalse(isRunning)
            assertEquals(listOf(), callbackAction)

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
    fun testColorFilter() = runTest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val context = InstrumentationRegistry.getInstrumentation().context
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
                .let { ByteBuffer.wrap(it) }
                .let { ImageDecoder.createSource(it) }
                .let { ImageDecoder.decodeDrawable(it) }
                .let { it as AnimatedImageDrawable }
                .let { ScaledAnimatableDrawable(it) }
                .apply {
                    if (Build.VERSION.SDK_INT >= 21) {
                        assertNull(colorFilter)
                    }
                    colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
                    if (Build.VERSION.SDK_INT >= 21) {
                        assertTrue(
                            colorFilter is PorterDuffColorFilter,
                            message = "colorFilter=$colorFilter"
                        )
                    }
                }
        }
    }

    @Test
    fun testChange() {
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testOpacity() = runTest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val context = InstrumentationRegistry.getInstrumentation().context
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
                .let { ByteBuffer.wrap(it) }
                .let { ImageDecoder.createSource(it) }
                .let { ImageDecoder.decodeDrawable(it) }
                .let { it as AnimatedImageDrawable }
                .let { ScaledAnimatableDrawable(it) }
                .apply {
                    assertEquals(PixelFormat.TRANSLUCENT, opacity)

                    start()
                    assertEquals(PixelFormat.TRANSLUCENT, opacity)

                    stop()
                    assertEquals(PixelFormat.TRANSLUCENT, opacity)
                }
        }
    }

    @Test
    fun testDraw() {
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
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
        ScaledAnimatableDrawable(
            TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        ).apply {
            assertEquals(Rect(0, 0, 0, 0), bounds)
            setBounds(0, 0, 100, 200)
            assertEquals(Rect(0, 0, 100, 200), bounds)
        }
    }

    @Test
    fun testAlpha() = runTest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val context = InstrumentationRegistry.getInstrumentation().context
            ComposeResImageFiles.animGif
                .toDataSource(context).openSource()
                .buffer().use { it.readByteArray() }
                .let { ByteBuffer.wrap(it) }
                .let { ImageDecoder.createSource(it) }
                .let { ImageDecoder.decodeDrawable(it) }
                .let { it as AnimatedImageDrawable }
                .let { ScaledAnimatableDrawable(it) }.apply {
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
    }

    @Test
    fun testEqualsAndHashCode() {
        val animatedImageDrawable = TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        val animatedImageDrawable2 = TestAnimatable2CompatDrawable(ColorDrawable(Color.BLUE))
        val element1 = ScaledAnimatableDrawable(animatedImageDrawable, true)
        val element11 = ScaledAnimatableDrawable(animatedImageDrawable, true)
        val element2 = ScaledAnimatableDrawable(animatedImageDrawable2, true)
        val element3 = ScaledAnimatableDrawable(animatedImageDrawable, false)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val animatedImageDrawable = TestAnimatableDrawable(ColorDrawable(Color.BLUE))
        assertEquals(
            "ScaledAnimatableDrawable(drawable=${animatedImageDrawable.toLogString()}, fitScale=true)",
            ScaledAnimatableDrawable(animatedImageDrawable).toString()
        )
    }
}