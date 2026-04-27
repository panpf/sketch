package com.github.panpf.sketch.http.okhttp.common.test.util

import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.OkHttpHttpComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OkHttpHttpComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = OkHttpHttpComponentProvider()
        assertEquals(
            expected = "[OkHttpHttpUriFetcher]",
            actual = componentProvider.addFetchers(context).toString()
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
        val element1 = OkHttpHttpComponentProvider()
        val element11 = OkHttpHttpComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "OkHttpComponentProvider",
            actual = OkHttpHttpComponentProvider().toString()
        )
    }
}