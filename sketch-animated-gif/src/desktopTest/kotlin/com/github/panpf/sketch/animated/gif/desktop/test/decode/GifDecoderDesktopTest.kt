package com.github.panpf.sketch.animated.gif.desktop.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.supportGif
import kotlin.test.Test
import kotlin.test.assertEquals

class GifDecoderDesktopTest {

    @Test
    fun testSupportGif() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
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
                        "decoders=[SkiaGifDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
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
                        "decoders=[SkiaGifDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }
}