package com.github.panpf.sketch.blurhash.nonjvm.test.fetch.internal

import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.internal.BlurHashUriFetcherProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BlurHashUriFetcherProviderNonJvmTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val fetcherProvider = BlurHashUriFetcherProvider()
        val factory = fetcherProvider.factory(context)
        assertTrue(
            actual = factory is BlurHashUriFetcher.Factory,
            message = factory.toString()
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
        val fetcherProvider = BlurHashUriFetcherProvider()
        assertTrue(
            actual = fetcherProvider.toString().contains("BlurHashUriFetcherProvider"),
            message = fetcherProvider.toString()
        )
        assertTrue(
            actual = fetcherProvider.toString().contains("@"),
            message = fetcherProvider.toString()
        )
    }
}