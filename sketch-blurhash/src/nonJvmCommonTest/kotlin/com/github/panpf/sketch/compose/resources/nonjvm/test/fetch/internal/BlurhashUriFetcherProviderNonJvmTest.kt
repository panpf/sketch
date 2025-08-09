package com.github.panpf.sketch.compose.resources.nonjvm.test.fetch.internal

import com.github.panpf.sketch.fetch.BlurhashUriFetcher
import com.github.panpf.sketch.fetch.internal.BlurhashUriFetcherProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BlurhashUriFetcherProviderNonJvmTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = BlurhashUriFetcherProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertTrue(
            actual = decoderFactory is BlurhashUriFetcher.Factory,
            message = decoderFactory.toString()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurhashUriFetcherProvider()
        val element11 = BlurhashUriFetcherProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = BlurhashUriFetcherProvider()
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