package com.github.panpf.sketch.http.ktor2.nonjvm.test.util

import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.KtorHttpComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KtorHttpComponentProviderNonJvmTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = KtorHttpComponentProvider()
        assertEquals(
            expected = "[KtorHttpUriFetcher]",
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
        val element1 = KtorHttpComponentProvider()
        val element11 = KtorHttpComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "KtorHttpComponentProvider",
            actual = KtorHttpComponentProvider().toString()
        )
    }
}