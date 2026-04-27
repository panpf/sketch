package com.github.panpf.sketch.compose.resources.jvm.test.util

import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.ComposeResourceComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ComposeResourceComponentProviderJvmTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = ComposeResourceComponentProvider()
        assertEquals(
            expected = listOf(ComposeResourceUriFetcher.Factory()),
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
        val element1 = ComposeResourceComponentProvider()
        val element11 = ComposeResourceComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ComposeResourceComponentProvider",
            actual = ComposeResourceComponentProvider().toString()
        )
    }
}