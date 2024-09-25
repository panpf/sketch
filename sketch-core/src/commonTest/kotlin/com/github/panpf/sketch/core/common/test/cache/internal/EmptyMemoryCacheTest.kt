package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.cache.internal.EmptyMemoryCache
import com.github.panpf.sketch.test.utils.FakeImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EmptyMemoryCacheTest {

    @Test
    fun test() {
        val emptyDiskCache = EmptyMemoryCache
        assertEquals(expected = 0L, actual = emptyDiskCache.size)
        assertEquals(expected = 0L, actual = emptyDiskCache.maxSize)
        assertEquals(
            expected = -3,
            actual = emptyDiskCache.put("key", ImageCacheValue(FakeImage(100, 100)))
        )
        assertEquals(expected = false, actual = emptyDiskCache.exist("key"))
        assertEquals(expected = null, actual = emptyDiskCache.remove("key"))
        assertEquals(expected = null, actual = emptyDiskCache.get("key"))
        assertEquals(expected = emptySet(), actual = emptyDiskCache.keys())
    }

    @Test
    fun testWithLock() {
        // TODO testWithLock
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = EmptyMemoryCache
        val element11 = EmptyMemoryCache

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "EmptyMemoryCache",
            actual = EmptyMemoryCache.toString()
        )
    }
}