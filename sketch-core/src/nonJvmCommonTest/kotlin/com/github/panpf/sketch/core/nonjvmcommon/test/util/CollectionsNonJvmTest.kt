package com.github.panpf.sketch.core.nonjvmcommon.test.util

import com.github.panpf.sketch.util.LruMutableMap
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionsNonJvmTest {

    @Test
    fun testLruMutableMap() {
        assertEquals(
            expected = true,
            actual = LruMutableMap<String, String>() is LruMutableMap
        )

        val cache = LruMutableMap<String, Int>()
        cache["a"] = 1
        cache["b"] = 2
        cache["c"] = 3
        cache["d"] = 4
        assertEquals(
            expected = "{a=1, b=2, c=3, d=4}",
            actual = cache.entries
                .joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        )

        cache["c"]
        assertEquals(
            expected = "{a=1, b=2, d=4, c=3}",
            actual = cache.entries
                .joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        )

        cache["b"]
        assertEquals(
            expected = "{a=1, d=4, c=3, b=2}",
            actual = cache.entries
                .joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        )

        cache["a"]
        assertEquals(
            expected = "{d=4, c=3, b=2, a=1}",
            actual = cache.entries
                .joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        )
    }

    @Test
    fun testToImmutableMap() {
        // TODO test
    }

    @Test
    fun testToImmutableList() {
        // TODO test
    }

    @Test
    fun testImmutableMap() {
        // TODO test
    }

    @Test
    fun testImmutableList() {
        // TODO test
    }
}