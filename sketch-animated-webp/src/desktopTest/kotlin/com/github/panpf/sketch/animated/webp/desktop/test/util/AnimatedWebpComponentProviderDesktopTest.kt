package com.github.panpf.sketch.animated.webp.desktop.test.util

import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.AnimatedWebpComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AnimatedWebpComponentProviderDesktopTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = AnimatedWebpComponentProvider()
        assertEquals(
            expected = null,
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = listOf(SkiaAnimatedWebpDecoder.Factory()),
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
        val element1 = AnimatedWebpComponentProvider()
        val element11 = AnimatedWebpComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "AnimatedWebpComponentProvider",
            actual = AnimatedWebpComponentProvider().toString()
        )
    }
}