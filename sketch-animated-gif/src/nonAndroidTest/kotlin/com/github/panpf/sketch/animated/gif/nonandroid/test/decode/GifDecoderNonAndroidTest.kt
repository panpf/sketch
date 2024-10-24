package com.github.panpf.sketch.animated.gif.nonandroid.test.decode

import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.decode.defaultGifDecoderFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class GifDecoderNonAndroidTest {

    @Test
    fun testDefaultGifDecoderFactory() {
        assertEquals(expected = SkiaGifDecoder.Factory(), actual = defaultGifDecoderFactory())
    }
}