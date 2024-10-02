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

import android.graphics.Color
import android.graphics.drawable.Animatable2
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2Drawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResizeAnimatableDrawableTest {

    @Test
    fun testCallback() = runTest {
        // Animatable2
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val animatedDrawable = TestAnimatable2Drawable(ColorDrawable(Color.GREEN))
            val wrapper = ResizeAnimatableDrawable(animatedDrawable, Size(100, 500), CENTER_CROP)
            assertEquals(expected = 0, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 1,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 2,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 3,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 2,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
        }

        // Animatable2Compat
        runBlock {
            val animatable2Drawable = TestAnimatable2CompatDrawable(ColorDrawable(Color.GREEN))
            val wrapper = ResizeAnimatableDrawable(animatable2Drawable, Size(100, 500), CENTER_CROP)
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
        }

        // Animatable
        runBlock {
            val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.GREEN))
            val wrapper = ResizeAnimatableDrawable(animatableDrawable, Size(100, 500), CENTER_CROP)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(
                expected = 1,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(
                expected = 2,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(
                expected = 3,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(
                expected = 2,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )

            wrapper.clearAnimationCallbacks()
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbacksInternal?.size ?: 0
            )
            assertEquals(
                expected = 0,
                actual = wrapper.callbackHelper?.callbackProxyMapInternal?.size ?: 0
            )
        }
    }

    @Test
    fun testStartStop() = runTest {
        val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.YELLOW))
        val wrapper = ResizeAnimatableDrawable(animatableDrawable, Size(100, 500), CENTER_CROP)

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

        ResizeAnimatableDrawable(
            AnimatableDrawable(
                TestAnimatableDrawable(context.getDrawableCompat(android.R.drawable.ic_lock_lock))
            ),
            Size(500, 300),
            CENTER_CROP
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

        ResizeAnimatableDrawable(
            TestAnimatableDrawable(
                TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.ic_lock_lock))
            ),
            Size(500, 300),
            CENTER_CROP
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
        val element1 = ResizeAnimatableDrawable(
            drawable = TestAnimatableDrawable(ColorDrawable(TestColor.RED).asEquitable()),
            size = Size(100, 500),
            scale = CENTER_CROP,
        )
        val element11 = ResizeAnimatableDrawable(
            drawable = TestAnimatableDrawable(ColorDrawable(TestColor.RED).asEquitable()),
            size = Size(100, 500),
            scale = CENTER_CROP,
        )
        val element2 = ResizeAnimatableDrawable(
            drawable = TestAnimatableDrawable(ColorDrawable(TestColor.GREEN).asEquitable()),
            size = Size(100, 500),
            scale = CENTER_CROP,
        )
        val element3 = ResizeAnimatableDrawable(
            drawable = TestAnimatableDrawable(ColorDrawable(TestColor.RED).asEquitable()),
            size = Size(500, 100),
            scale = CENTER_CROP,
        )
        val element4 = ResizeAnimatableDrawable(
            drawable = TestAnimatableDrawable(ColorDrawable(TestColor.RED).asEquitable()),
            size = Size(100, 500),
            scale = START_CROP,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.GREEN))
        val sketchAnimatableDrawable = AnimatableDrawable(animatableDrawable)
        ResizeAnimatableDrawable(
            sketchAnimatableDrawable,
            Size(100, 500),
            CENTER_CROP
        ).apply {
            assertEquals(
                "ResizeAnimatableDrawable(drawable=${sketchAnimatableDrawable.toLogString()}, size=100x500, scale=CENTER_CROP)",
                toString()
            )
        }
    }

    private val AnimatableCallbackHelper.callbackProxyMapInternal: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>?
        get() {
            return getFieldValue("callbackProxyMap")
        }

    private val AnimatableCallbackHelper.callbacksInternal: MutableList<Animatable2Compat.AnimationCallback>?
        get() {
            return getFieldValue("callbacks")
        }
}
