package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.EquitableAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2Drawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.toLogString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EquitableAnimatableDrawableTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = "EquitableAnimatableDrawable('${key(TestColor.RED)}')",
            actual = EquitableAnimatableDrawable(
                drawable = TestAnimatable2CompatDrawable(ColorDrawable(TestColor.RED)),
                equalityKey = TestColor.RED
            ).key
        )
    }

    @Test
    fun testAcceptType() {
        val context = getTestContext()

        val bitmapDrawable = context.getDrawableCompat(android.R.drawable.ic_delete)
        assertFailsWith(IllegalArgumentException::class) {
            EquitableAnimatableDrawable(bitmapDrawable, equalityKey = "key")
        }

        val animatedDrawable = TestAnimatable2CompatDrawable(ColorDrawable(Color.GREEN))
        val wrapper = EquitableAnimatableDrawable(animatedDrawable, equalityKey = "key")

        assertFailsWith(IllegalArgumentException::class) {
            wrapper.drawable = bitmapDrawable
        }
    }

    @Test
    fun testCallback() = runTest {
        // Animatable2
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val animatedDrawable = TestAnimatable2Drawable(ColorDrawable(Color.GREEN))
            val wrapper = EquitableAnimatableDrawable(animatedDrawable, equalityKey = "key")
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
            val wrapper = EquitableAnimatableDrawable(animatable2Drawable, equalityKey = "key")
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
            val wrapper = EquitableAnimatableDrawable(animatableDrawable, equalityKey = "key")
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
        val animatableDrawable = TestAnimatableDrawable()
        val wrapper = EquitableAnimatableDrawable(animatableDrawable, equalityKey = "key")

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
    fun testEqualsAndHashCode() {
        val element1 = EquitableAnimatableDrawable(
            drawable = TestAnimatable2CompatDrawable(ColorDrawable(TestColor.RED)),
            equalityKey = TestColor.RED
        )
        val element11 = EquitableAnimatableDrawable(
            drawable = TestAnimatable2CompatDrawable(ColorDrawable(TestColor.RED)),
            equalityKey = TestColor.RED
        )
        val element2 = EquitableAnimatableDrawable(
            drawable = TestAnimatable2CompatDrawable(ColorDrawable(TestColor.RED)),
            equalityKey = TestColor.CYAN
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val drawable = TestAnimatable2CompatDrawable(ColorDrawable(TestColor.RED))
        assertEquals(
            expected = "EquitableAnimatableDrawable(drawable=${drawable.toLogString()}, equalityKey=${TestColor.RED})",
            actual = EquitableAnimatableDrawable(
                drawable = drawable,
                equalityKey = TestColor.RED,
            ).toString()
        )
    }
}