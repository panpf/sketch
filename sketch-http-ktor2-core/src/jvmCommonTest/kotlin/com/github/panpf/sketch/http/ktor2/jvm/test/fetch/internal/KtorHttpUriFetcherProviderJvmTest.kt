package com.github.panpf.sketch.http.ktor2.jvm.test.fetch.internal

import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class KtorHttpUriFetcherProviderJvmTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = KtorHttpUriFetcherProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertTrue(
            actual = decoderFactory is KtorHttpUriFetcher.Factory,
            message = decoderFactory.toString()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = KtorHttpUriFetcherProvider()
        val element11 = KtorHttpUriFetcherProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = KtorHttpUriFetcherProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("KtorHttpUriFetcherProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}