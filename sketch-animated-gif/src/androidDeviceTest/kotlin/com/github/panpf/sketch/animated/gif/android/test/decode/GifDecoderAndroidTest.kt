package com.github.panpf.sketch.animated.gif.android.test.decode

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.decode.defaultGifDecoderFactory
import com.github.panpf.sketch.decode.supportGif
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GifDecoderAndroidTest {

    @Test
    fun testSupportGif() {
        val factory = defaultGifDecoderFactory()

        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[${factory}]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportGif()
            supportGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[${factory},${factory}]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testDefaultGifDecoderFactory() {
        val decoderFactory = defaultGifDecoderFactory()
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertTrue(decoderFactory is ImageDecoderGifDecoder.Factory)
        } else {
            assertTrue(decoderFactory is MovieGifDecoder.Factory)
        }
    }
}