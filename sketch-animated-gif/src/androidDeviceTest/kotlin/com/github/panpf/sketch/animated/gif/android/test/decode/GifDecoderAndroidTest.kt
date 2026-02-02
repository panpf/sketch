package com.github.panpf.sketch.animated.gif.android.test.decode

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.defaultGifDecoderFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class GifDecoderAndroidTest {

    @Test
    fun testDefaultGifDecoderFactory() {
        assertEquals(
            expected = if (VERSION.SDK_INT >= VERSION_CODES.P)
                "ImageDecoderGifDecoder" else "MovieGifDecoder",
            actual = defaultGifDecoderFactory().toString()
        )
    }
}