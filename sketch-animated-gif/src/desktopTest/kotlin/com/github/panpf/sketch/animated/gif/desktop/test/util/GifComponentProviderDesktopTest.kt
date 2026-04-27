package com.github.panpf.sketch.animated.gif.desktop.test.util

import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.GifComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GifComponentProviderDesktopTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = GifComponentProvider()
        assertEquals(
            expected = null,
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = listOf(SkiaGifDecoder.Factory()),
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
        val element1 = GifComponentProvider()
        val element11 = GifComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "GifComponentProvider",
            actual = GifComponentProvider().toString()
        )
    }
}