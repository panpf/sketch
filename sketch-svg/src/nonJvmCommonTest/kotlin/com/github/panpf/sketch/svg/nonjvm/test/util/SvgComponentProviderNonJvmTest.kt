package com.github.panpf.sketch.svg.nonjvm.test.util

import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.SvgComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SvgComponentProviderNonJvmTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = SvgComponentProvider()
        assertEquals(
            expected = null,
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = listOf(SvgDecoder.Factory()),
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
        val element1 = SvgComponentProvider()
        val element11 = SvgComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "SvgComponentProvider",
            actual = SvgComponentProvider().toString()
        )
    }
}