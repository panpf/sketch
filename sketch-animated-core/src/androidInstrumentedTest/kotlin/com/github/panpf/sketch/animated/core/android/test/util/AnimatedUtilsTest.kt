package com.github.panpf.sketch.animated.core.android.test.util

import com.github.panpf.sketch.util.animatable2CompatCallbackOf
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AnimatedUtilsTest {

    @Test
    fun testRequiredMainThread() = runTest {
        assertFailsWith(IllegalStateException::class) {
            requiredMainThread()
        }
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testAnimatable2CompatCallbackOf() {
        animatable2CompatCallbackOf(onStart = null, onEnd = null).apply {
            onAnimationStart(null)
            onAnimationEnd(null)
        }

        animatable2CompatCallbackOf(onStart = {}, onEnd = { }).apply {
            onAnimationStart(null)
            onAnimationEnd(null)
        }
    }
}