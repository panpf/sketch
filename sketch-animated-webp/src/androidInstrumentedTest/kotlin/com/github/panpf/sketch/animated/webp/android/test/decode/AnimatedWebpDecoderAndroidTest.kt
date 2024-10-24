package com.github.panpf.sketch.animated.webp.android.test.decode

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.defaultAnimatedWebpDecoderFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedWebpDecoderAndroidTest {

    @Test
    fun testDefaultAnimatedWebpDecoderFactory() {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertEquals(
                expected = ImageDecoderAnimatedWebpDecoder.Factory(),
                actual = defaultAnimatedWebpDecoderFactory()
            )
        } else {
            assertEquals(
                expected = null,
                actual = defaultAnimatedWebpDecoderFactory()
            )
        }
    }
}