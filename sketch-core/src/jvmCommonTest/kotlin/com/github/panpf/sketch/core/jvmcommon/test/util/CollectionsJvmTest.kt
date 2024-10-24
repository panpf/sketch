package com.github.panpf.sketch.core.jvmcommon.test.util

import com.github.panpf.sketch.util.LruMutableMap
import com.github.panpf.sketch.util.toImmutableList
import com.github.panpf.sketch.util.toImmutableMap
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionsJvmTest {

    @Test
    fun testLruMutableMap() {
        assertEquals(
            expected = true,
            actual = LruMutableMap<String, String>() is LinkedHashMap
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
        assertEquals(
            expected = "class kotlin.collections.EmptyMap",
            actual = mapOf<String, Int>().toImmutableMap()::class.java.toString()
        )
        assertEquals(
            expected = "class java.util.Collections\$SingletonMap",
            actual = mapOf("key" to 2).toImmutableMap()::class.java.toString()
        )
        assertEquals(
            expected = "class java.util.Collections\$UnmodifiableMap",
            actual = mapOf("key" to 2, "key2" to 4).toImmutableMap()::class.java.toString()
        )
    }

    @Test
    fun testToImmutableList() {
        assertEquals(
            expected = "class kotlin.collections.EmptyList",
            actual = listOf<String>().toImmutableList()::class.java.toString()
        )
        assertEquals(
            expected = "class java.util.Collections\$SingletonList",
            actual = listOf("key").toImmutableList()::class.java.toString()
        )
        assertEquals(
            expected = "class java.util.Collections\$UnmodifiableRandomAccessList",
            actual = listOf("key", "key2").toImmutableList()::class.java.toString()
        )
    }
}