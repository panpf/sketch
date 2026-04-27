package com.github.panpf.sketch.animated.webp.android.test.decode

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.defaultAnimatedWebpDecoderFactory
import com.github.panpf.sketch.decode.supportAnimatedWebp
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedWebpDecoderAndroidTest {

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

        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            ComponentRegistry {
                supportAnimatedWebp()
            }.apply {
                assertEquals(
                    expected = "ComponentRegistry(" +
                            "fetchers=[]," +
                            "decoders=[ImageDecoderAnimatedWebpDecoder]," +
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
                            "decoders=[ImageDecoderAnimatedWebpDecoder]," +
                            "interceptors=[]," +
                            "disabledFetchers=[]," +
                            "disabledDecoders=[]," +
                            "disabledInterceptors=[]" +
                            ")",
                    actual = toString()
                )
            }
        } else {
            ComponentRegistry {
                supportAnimatedWebp()
            }.apply {
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
                supportAnimatedWebp()
                supportAnimatedWebp()
            }.apply {
                assertEquals(
                    expected = "ComponentRegistry(" +
                            "fetchers=[]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    actual = toString()
                )
            }
        }
    }

    @Test
    fun testDefaultAnimatedWebpDecoderFactory() {
        val decoderFactory = defaultAnimatedWebpDecoderFactory()
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertEquals(
                expected = ImageDecoderAnimatedWebpDecoder.Factory(),
                actual = decoderFactory
            )
        } else {
            assertEquals(
                expected = null,
                actual = decoderFactory
            )
        }
    }
}