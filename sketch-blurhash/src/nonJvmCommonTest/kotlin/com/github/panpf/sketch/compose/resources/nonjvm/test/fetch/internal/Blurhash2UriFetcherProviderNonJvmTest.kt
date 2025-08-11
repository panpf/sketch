package com.github.panpf.sketch.compose.resources.nonjvm.test.fetch.internal

import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.internal.BlurHashUriFetcherProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class Blurhash2UriFetcherProviderNonJvmTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = BlurHashUriFetcherProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertTrue(
            actual = decoderFactory is BlurHashUriFetcher.Factory,
            message = decoderFactory.toString()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashUriFetcherProvider()
        val element11 = BlurHashUriFetcherProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = BlurHashUriFetcherProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("BlurhashUriFetcherProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}