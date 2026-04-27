package com.github.panpf.sketch.extensions.appicon.test.util

import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.AppIconComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AppIconComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = AppIconComponentProvider()
        assertEquals(
            expected = listOf(AppIconUriFetcher.Factory()),
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
        val element1 = AppIconComponentProvider()
        val element11 = AppIconComponentProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "AppIconComponentProvider",
            actual = AppIconComponentProvider().toString()
        )
    }
}