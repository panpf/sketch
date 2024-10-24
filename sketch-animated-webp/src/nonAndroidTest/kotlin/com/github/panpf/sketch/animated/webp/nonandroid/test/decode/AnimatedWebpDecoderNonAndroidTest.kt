package com.github.panpf.sketch.animated.webp.nonandroid.test.decode

import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder
import com.github.panpf.sketch.decode.defaultAnimatedWebpDecoderFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedWebpDecoderNonAndroidTest {

    @Test
    fun testDefaultAnimatedWebpDecoderFactory() {
        assertEquals(
            expected = SkiaAnimatedWebpDecoder.Factory(),
            actual = defaultAnimatedWebpDecoderFactory()
        )
    }
}