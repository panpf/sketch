package com.github.panpf.sketch.core.android.test.drawable.internal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2Drawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.util.getDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimatableCallbackHelperTest {

    @Test
    fun testSetDrawable() {
        val context = getTestContext()

        val bitmapDrawable = context.getDrawableCompat(android.R.drawable.ic_delete)
        assertFailsWith(IllegalArgumentException::class) {
            AnimatableCallbackHelper(bitmapDrawable)
        }

        val animatedDrawable = TestAnimatable2CompatDrawable(ColorDrawable(Color.GREEN))
        val wrapper = AnimatableCallbackHelper(animatedDrawable)

        assertFailsWith(IllegalArgumentException::class) {
            wrapper.setDrawable(bitmapDrawable)
        }
    }

    @Test
    fun testCallback() = runTest {
        // Animatable2
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val animatedDrawable = TestAnimatable2Drawable(ColorDrawable(Color.GREEN))
            val wrapper = AnimatableCallbackHelper(animatedDrawable)
            assertEquals(expected = 0, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 1, actual = wrapper.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 2, actual = wrapper.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 3, actual = wrapper.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 2, actual = wrapper.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatedDrawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
        }

        // Animatable2Compat
        runBlock {
            val animatable2Drawable = TestAnimatable2CompatDrawable(ColorDrawable(Color.GREEN))
            val wrapper = AnimatableCallbackHelper(animatable2Drawable)
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(
                expected = 0,
                actual = wrapper.callbackProxyMap?.size ?: 0
            )

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)
        }

        // Animatable
        runBlock {
            val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.GREEN))
            val wrapper = AnimatableCallbackHelper(animatableDrawable)
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = wrapper.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap?.size ?: 0)
        }
    }

    @Test
    fun testStartStop() = runTest {
        val animatableDrawable = TestAnimatableDrawable(ColorDrawable(Color.YELLOW))
        val wrapper = AnimatableCallbackHelper(animatableDrawable)

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
}