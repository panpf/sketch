package com.github.panpf.sketch.core.android.test.drawable.internal

import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.internal.AnimatableDrawableWrapper
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.buffer
import java.nio.ByteBuffer
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimatableDrawableWrapperTest {

    @Test
    fun test() {
        val context = getTestContext()

        val bitmapDrawable = context.getDrawableCompat(android.R.drawable.ic_delete)
        assertFailsWith(IllegalArgumentException::class) {
            AnimatableDrawableWrapper(bitmapDrawable)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val bytes = ResourceImages.animGif.toDataSource(context).openSource()
                .buffer()
                .use { it.readByteArray() }
            val animatedDrawable =
                ImageDecoder.decodeDrawable(ImageDecoder.createSource(ByteBuffer.wrap(bytes)))
                    .asOrThrow<AnimatedImageDrawable>()
            val wrapper = AnimatableDrawableWrapper(animatedDrawable)

            assertFailsWith(IllegalArgumentException::class) {
                wrapper.drawable = bitmapDrawable
            }
        }
    }

    @Test
    fun testCallback() = runTest {
        val context = getTestContext()

        // Animatable2
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val bytes = ResourceImages.animGif.toDataSource(context).openSource()
                .buffer()
                .use { it.readByteArray() }
            val animatedDrawable =
                ImageDecoder.decodeDrawable(ImageDecoder.createSource(ByteBuffer.wrap(bytes)))
                    .asOrThrow<AnimatedImageDrawable>()
            val wrapper = AnimatableDrawableWrapper(animatedDrawable)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = wrapper.callbackProxyMap.size)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = wrapper.callbackProxyMap.size)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = wrapper.callbackProxyMap.size)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = wrapper.callbackProxyMap.size)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
        }

        // Animatable2Compat
        runBlock {
            val animatable2Drawable = TestAnimatable2CompatDrawable()
            val wrapper = AnimatableDrawableWrapper(animatable2Drawable)
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = animatable2Drawable.callbacks?.size ?: 0)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)
        }

        // Animatable
        runBlock {
            val animatableDrawable = TestAnimatableDrawable()
            val wrapper = AnimatableDrawableWrapper(animatableDrawable)
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 1, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            val callback2 = object : Animatable2Compat.AnimationCallback() {}
            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(callback2)
            }
            assertEquals(expected = 2, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            withContext(Dispatchers.Main) {
                wrapper.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {})
            }
            assertEquals(expected = 3, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            wrapper.unregisterAnimationCallback(callback2)
            assertEquals(expected = 2, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)

            wrapper.clearAnimationCallbacks()
            assertEquals(expected = 0, actual = wrapper.callbacks.size)
            assertEquals(expected = 0, actual = wrapper.callbackProxyMap.size)
        }
    }

    @Test
    fun testStartStop() = runTest {
        val animatableDrawable = TestAnimatableDrawable()
        val wrapper = AnimatableDrawableWrapper(animatableDrawable)

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

    private val AnimatableDrawableWrapper.callbackProxyMap: Map<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>
        get() {
            return this::class.declaredMemberProperties
                .find { it.name == "callbackProxyMap" }!!
                .asOrThrow<KProperty1<AnimatableDrawableWrapper, Map<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>?>>()
                .apply { isAccessible = true }
                .get(this)
                ?: emptyMap()
        }

    private val AnimatableDrawableWrapper.callbacks: List<Animatable2Compat.AnimationCallback>
        get() {
            return this::class.declaredMemberProperties
                .find { it.name == "callbacks" }!!
                .asOrThrow<KProperty1<AnimatableDrawableWrapper, List<Animatable2Compat.AnimationCallback>?>>()
                .apply { isAccessible = true }
                .get(this)
                ?: emptyList()
        }
}