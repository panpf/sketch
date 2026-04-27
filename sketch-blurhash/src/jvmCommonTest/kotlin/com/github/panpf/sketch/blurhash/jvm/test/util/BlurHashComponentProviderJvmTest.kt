package com.github.panpf.sketch.blurhash.jvm.test.util

import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.BlurHashComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlurHashComponentProviderJvmTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = BlurHashComponentProvider()
        assertEquals(
            expected = listOf(BlurHashUriFetcher.Factory()),
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = listOf(BlurHashDecoder.Factory()),
            actual = componentProvider.addDecoders(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.addInterceptors(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.disabledFetchers(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.disabledDecoders(context)
        )
        assertEquals(
            expected = null,
            actual = componentProvider.disabledInterceptors(context)
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashComponentProvider()
        val element11 = BlurHashComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "BlurHashComponentProvider",
            actual = BlurHashComponentProvider().toString()
        )
    }
}