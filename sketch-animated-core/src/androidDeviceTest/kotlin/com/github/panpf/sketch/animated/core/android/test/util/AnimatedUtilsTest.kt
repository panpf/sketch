package com.github.panpf.sketch.animated.core.android.test.util

import com.github.panpf.sketch.util.animatable2CompatCallbackOf
import kotlin.test.Test

class AnimatedUtilsTest {

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