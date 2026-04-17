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

package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2Drawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.util.toLogString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AnimatableDrawableTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        AnimatableDrawable(
            TestAnimatableDrawable(
                BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
            ),
        )
        assertFailsWith(IllegalArgumentException::class) {
            AnimatableDrawable(
                BitmapDrawable(
                    context.resources,
                    Bitmap.createBitmap(100, 100, ARGB_8888)
                ),
            )
        }
    }

    @Test
    fun testCallback() = runTest {
        // Animatable2
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            val animatedDrawable = TestAnimatable2Drawable(ColorDrawable(Color.GREEN))
            val wrapper = AnimatableDrawable(animatedDrawable)
            assertEquals(expected = 0, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 1, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 2, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 3, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 2, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
        }

        // Animatable2Compat
        runBlock {
            val animatable2Drawable = TestAnimatable2CompatDrawable(ColorDrawable(Color.GREEN))
            val wrapper = AnimatableDrawable(animatable2Drawable)
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0
            )

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
        }

        // Animatable
        runBlock {
            val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.GREEN))
            val wrapper = AnimatableDrawable(animatableDrawable)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackHelper?.callbackProxyMap?.size ?: 0)
        }
    }

    @Test
    fun testStartStop() = runTest {
        val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.YELLOW))
        val wrapper = AnimatableDrawable(animatableDrawable)

        val callbackHistory = mutableListOf<String>()
        withContext(Dispatchers.Main) {
            wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    super.onAnimationStart(drawable)
                    callbackHistory.add("onAnimationStart")
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    callbackHistory.add("onAnimationEnd")
                }
            })
        }

        assertFalse(actual = animatableDrawable.isRunning)
        assertFalse(actual = wrapper.isRunning)
        assertEquals(expected = listOf(), actual = callbackHistory)

        wrapper.start()
        block(100)
        assertTrue(actual = animatableDrawable.isRunning)
        assertTrue(actual = wrapper.isRunning)
        assertEquals(expected = listOf("onAnimationStart"), actual = callbackHistory)

        wrapper.start()
        block(100)
        assertTrue(actual = animatableDrawable.isRunning)
        assertTrue(actual = wrapper.isRunning)
        assertEquals(expected = listOf("onAnimationStart"), actual = callbackHistory)

        wrapper.stop()
        block(100)
        assertFalse(actual = animatableDrawable.isRunning)
        assertFalse(actual = wrapper.isRunning)
        assertEquals(
            expected = listOf("onAnimationStart", "onAnimationEnd"),
            actual = callbackHistory
        )
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        AnimatableDrawable(
            TestAnimatableDrawable(context.getDrawableCompat(android.R.drawable.ic_lock_lock)),
        ).apply {
            val mutateDrawable = mutate()
            assertSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_lock_lock).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        AnimatableDrawable(
            TestAnimatableDrawable(TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.ic_lock_lock))),
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_lock_lock).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()

        val drawable = TestAnimatableDrawable(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )
        val drawable1 = TestAnimatableDrawable(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )

        val element1 = AnimatableDrawable(drawable)
        val element11 = AnimatableDrawable(drawable)
        val element2 = AnimatableDrawable(drawable1)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()

        val drawable = TestAnimatableDrawable(
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, ARGB_8888)),
        )

        val animatableDrawable = AnimatableDrawable(drawable)
        assertEquals(
            "AnimatableDrawable(drawable=${drawable.toLogString()})",
            animatableDrawable.toString()
        )
    }
}