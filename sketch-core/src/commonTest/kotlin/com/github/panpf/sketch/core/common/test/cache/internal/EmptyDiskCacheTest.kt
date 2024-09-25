package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.cache.internal.EmptyDiskCache
import com.github.panpf.sketch.util.defaultFileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EmptyDiskCacheTest {

    @Test
    fun test() {
        val emptyDiskCache = EmptyDiskCache(defaultFileSystem())
        assertEquals(expected = 0L, actual = emptyDiskCache.size)
        assertEquals(expected = 0L, actual = emptyDiskCache.maxSize)
        assertEquals(expected = "".toPath(), actual = emptyDiskCache.directory)
        assertEquals(expected = 0, actual = emptyDiskCache.appVersion)
        assertEquals(expected = 0, actual = emptyDiskCache.internalVersion)
        assertEquals(expected = null, actual = emptyDiskCache.openEditor("key"))
        assertEquals(expected = false, actual = emptyDiskCache.remove("key"))
        assertEquals(expected = null, actual = emptyDiskCache.openSnapshot("key"))
    }

    @Test
    fun testWithLock() {
        // TODO testWithLock
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = EmptyDiskCache(defaultFileSystem())
        val element11 = EmptyDiskCache(defaultFileSystem())

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "EmptyDiskCache",
            actual = EmptyDiskCache(defaultFileSystem()).toString()
        )
    }
}