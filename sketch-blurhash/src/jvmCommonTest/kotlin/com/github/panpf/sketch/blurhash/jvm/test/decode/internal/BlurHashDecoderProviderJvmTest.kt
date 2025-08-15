package com.github.panpf.sketch.blurhash.jvm.test.decode.internal

import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.decode.internal.BlurHashDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BlurHashDecoderProviderJvmTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val provider = BlurHashDecoderProvider()
        val factory = provider.factory(context)
        assertTrue(
            actual = factory is BlurHashDecoder.Factory,
            message = factory.toString()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashDecoderProvider()
        val element11 = BlurHashDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val provider = BlurHashDecoderProvider()
        assertTrue(
            actual = provider.toString().contains("BlurHashDecoderProvider"),
            message = provider.toString()
        )
        assertTrue(
            actual = provider.toString().contains("@"),
            message = provider.toString()
        )
    }
}