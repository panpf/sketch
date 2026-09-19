package com.github.panpf.sketch.animated.webp.jvm.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.supportAnimatedWebp
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedWebpDecoderJvmTest {

    @Test
    fun testSupportAnimatedWebp() {
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
            supportAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[SkiaAnimatedWebpDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAnimatedWebp()
            supportAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[SkiaAnimatedWebpDecoder]," +
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