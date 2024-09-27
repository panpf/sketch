package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.cache.internal.EmptyDiskCache
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.test.runTest
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

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun testWithLock() {
//        runTest {
//            var value: String? = null
//            var initialCount = 0
//            val initialCountLock = SynchronizedObject()
//            val jobs = mutableListOf<Deferred<*>>()
//            repeat(10) { index ->
//                val job = async(ioCoroutineDispatcher()) {
//                    if (value == null) {
//                        println("init start: $index")
//                        value = "value"
//                        block(100 - (index * 10L))
//                        synchronized(initialCountLock) {
//                            initialCount++
//                        }
//                        println("init end: $index. initialCount=$initialCount")
//                    }
//                }
//                jobs.add(job)
//            }
//            jobs.awaitAll()
//            assertTrue(actual = initialCount > 1, message = "initialCount=$initialCount")
//        }

        val cache = EmptyDiskCache(defaultFileSystem())
        runTest {
            var value: String? = null
            var initialCount = 0
            val initialCountLock = SynchronizedObject()
            val jobs = mutableListOf<Deferred<*>>()
            repeat(10) { index ->
                val job = async(ioCoroutineDispatcher()) {
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