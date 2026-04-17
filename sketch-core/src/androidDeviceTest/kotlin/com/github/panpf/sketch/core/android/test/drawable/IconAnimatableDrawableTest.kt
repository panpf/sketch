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

import android.R
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.IconAnimatableDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.SizeDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2Drawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.asAnimatableDrawable
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
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
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class IconAnimatableDrawableTest {

    @Test
    fun testConstructor() {
        IconAnimatableDrawable(
            icon = TestAnimatableDrawable(SizeDrawable(ColorDrawable(Color.GREEN), Size(100, 100)))
        ).apply {
            assertTrue(icon is Animatable)
            assertNull(background)
            assertNull(iconSize)
            assertNull(iconTint)
        }

        IconAnimatableDrawable(
            icon = TestAnimatableDrawable(SizeDrawable(ColorDrawable(Color.GREEN), Size(100, 100))),
            background = ColorDrawable(Color.GREEN),
            iconSize = Size(69, 44),
            iconTint = Color.RED,
        ).apply {
            assertTrue(icon is Animatable)
            assertEquals(Color.GREEN, background!!.asOrThrow<ColorDrawable>().color)
            assertEquals(Size(69, 44), iconSize)
            assertEquals(Color.RED, iconTint)
        }

        assertFailsWith(IllegalArgumentException::class) {
            IconAnimatableDrawable(icon = SizeDrawable(ColorDrawable(Color.GREEN), Size(100, 100)))
        }
        assertFailsWith(IllegalArgumentException::class) {
            IconAnimatableDrawable(
                icon = TestAnimatableDrawable(
                    SizeDrawable(
                        ColorDrawable(Color.GREEN),
                        Size(100, 100)
                    )
                ),
                background = TestAnimatableDrawable(ColorDrawable(Color.GREEN))
            )
        }
    }

    @Test
    fun testIntrinsicSize() {
        IconAnimatableDrawable(
            icon = SizeColorDrawable(Color.RED, Size(101, 202)).asAnimatableDrawable()
                .asEquitableWithThis(),
            background = null
        ).apply {
            assertEquals(expected = Size(-1, -1), actual = intrinsicSize)
        }

        IconAnimatableDrawable(
            icon = SizeColorDrawable(Color.RED, Size(101, 202)).asAnimatableDrawable()
                .asEquitableWithThis(),
            background = SizeColorDrawable(Color.GRAY, Size(1000, 500)).asEquitable()
        ).apply {
            assertEquals(expected = Size(-1, -1), actual = intrinsicSize)
        }
    }

    @Test
    fun testCallback() = runTest {
        // Animatable2
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            val animatedDrawable =
                TestAnimatable2Drawable(SizeDrawable(ColorDrawable(Color.GREEN), Size(100, 100)))
            val wrapper = IconAnimatableDrawable(animatedDrawable)
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
            val animatable2Drawable = TestAnimatable2CompatDrawable(
                SizeDrawable(
                    ColorDrawable(Color.GREEN),
                    Size(100, 100)
                )
            )
            val wrapper = IconAnimatableDrawable(animatable2Drawable)
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
            val animatableDrawable =
                TestAnimatableDrawable(SizeDrawable(ColorDrawable(Color.GREEN), Size(100, 100)))
            val wrapper = IconAnimatableDrawable(animatableDrawable)
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
        val animatableDrawable =
            TestAnimatableDrawable(SizeDrawable(ColorDrawable(Color.YELLOW), Size(100, 100)))
        val wrapper = IconAnimatableDrawable(animatableDrawable)

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

        IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                context.getDrawableCompat(R.drawable.bottom_bar)
            ),
        ).apply {
            val mutateDrawable = mutate()
            assertSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(R.drawable.bottom_bar).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                TestNewMutateDrawable(context.getDrawableCompat(R.drawable.bottom_bar))
            ),
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(R.drawable.bottom_bar).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        IconAnimatableDrawable(
            icon = TestAnimatableDrawable(context.getDrawableCompat(R.drawable.bottom_bar)),
            background = TestNewMutateDrawable(ColorDrawable(Color.RED)),
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(R.drawable.bottom_bar).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                SizeDrawable(
                    ColorDrawable(TestColor.RED).asEquitable(),
                    Size(100, 100)
                )
            ),
        )
        val element11 = IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                SizeDrawable(
                    ColorDrawable(TestColor.RED).asEquitable(),
                    Size(100, 100)
                )
            ),
        )
        val element2 = IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                SizeDrawable(
                    ColorDrawable(TestColor.GREEN).asEquitable(),
                    Size(100, 100)
                )
            ),
        )
        val element3 = IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                SizeDrawable(
                    ColorDrawable(TestColor.RED).asEquitable(),
                    Size(100, 100)
                )
            ),
            background = ColorDrawable(TestColor.GRAY).asEquitable(),
        )
        val element4 = IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                SizeDrawable(
                    ColorDrawable(TestColor.RED).asEquitable(),
                    Size(100, 100)
                )
            ),
            iconSize = Size(69, 44),
        )
        val element5 = IconAnimatableDrawable(
            icon = TestAnimatableDrawable(
                SizeDrawable(
                    ColorDrawable(TestColor.RED).asEquitable(),
                    Size(100, 100)
                )
            ),
            iconTint = TestColor.BLUE,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        val drawable =
            TestAnimatableDrawable(SizeDrawable(ColorDrawable(TestColor.RED), Size(100, 100)))
        val background = ColorDrawable(TestColor.GRAY)
        assertEquals(
            expected = "IconAnimatableDrawable(icon=TestAnimatableDrawable(drawable=SizeDrawable(drawable=ColorDrawable(color=-65536), size=100x100)), background=ColorDrawable(color=-7829368), iconSize=69x44, iconTint=-16776961)",
            actual = IconAnimatableDrawable(
                icon = drawable,
                background = background,
                iconSize = Size(69, 44),
                iconTint = TestColor.BLUE
            ).toString()
        )
    }
}