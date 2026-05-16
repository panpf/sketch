package com.github.panpf.sketch.avif.ios.test.util

import com.github.panpf.sketch.decode.AppleAvifDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.AvifComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AvifComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = AvifComponentProvider()
        assertEquals(
            expected = null,
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = listOf(AppleAvifDecoder.Factory()),
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
        val element1 = AvifComponentProvider()
        val element11 = AvifComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "AvifComponentProvider",
            actual = AvifComponentProvider().toString()
        )
    }
}
