package com.github.panpf.sketch.animated.webp.ios.test.decode.internal

import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder
import com.github.panpf.sketch.decode.internal.AnimatedWebpDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AnimatedWebpDecoderProviderIosTest {

    @Test
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = AnimatedWebpDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertEquals(
            expected = SkiaAnimatedWebpDecoder.Factory(),
            actual = decoderFactory
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = AnimatedWebpDecoderProvider()
        val element11 = AnimatedWebpDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = AnimatedWebpDecoderProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("AnimatedWebpDecoderProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}