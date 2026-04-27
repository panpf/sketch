package com.github.panpf.sketch.http.hurl.common.test.util

import com.github.panpf.sketch.fetch.HurlHttpUriFetcher
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.HurlHttpComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HurlHttpComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = HurlHttpComponentProvider()
        assertEquals(
            expected = listOf(HurlHttpUriFetcher.Factory()),
            actual = componentProvider.addFetchers(context)
        )
        assertEquals(
            expected = null,
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
        val element1 = HurlHttpComponentProvider()
        val element11 = HurlHttpComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "HurlHttpComponentProvider",
            actual = HurlHttpComponentProvider().toString()
        )
    }
}