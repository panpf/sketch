package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.cache.internal.EmptyMemoryCache
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun testWithLock() {
        runTest {
            var value: String? = null
            var initialCount = 0
            val initialCountLock = SynchronizedObject()
            val jobs = mutableListOf<Deferred<*>>()
            repeat(10) { index ->
                val job = async(ioCoroutineDispatcher()) {
                    if (value == null) {
                        println("init start: $index")
                        value = "value"
                        block(100 - (index * 10L))
                        synchronized(initialCountLock) {
                            initialCount++
                        }
                        println("init end: $index. initialCount=$initialCount")
                    }
                }
                jobs.add(job)
            }
            jobs.awaitAll()
            assertTrue(actual = initialCount > 1, message = "initialCount=$initialCount")
        }

        val cache = EmptyMemoryCache
        runTest {
            launch(ioCoroutineDispatcher()) {
                assertFailsWith(IllegalStateException::class) {
                    cache.withLock("key") {

                    }
                }
            }.join()
        }
        runTest {
            var value: String? = null
            var initialCount = 0
            val initialCountLock = SynchronizedObject()
            val jobs = mutableListOf<Deferred<*>>()
            repeat(10) { index ->
                val job = async(Dispatchers.Main) {
                    cache.withLock("key") {
                        if (value == null) {
                            println("init start: $index")
                            value = "value"
                            block(100 - (index * 10L))
                            synchronized(initialCountLock) {
                                initialCount++
                            }
                            println("init end: $index. initialCount=$initialCount")
                        }
                    }
                }
                jobs.add(job)
            }
            jobs.awaitAll()
            assertEquals(expected = 1, actual = initialCount)
        }
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