/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.createCacheValue
import com.github.panpf.sketch.util.formatFileSize
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
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LruMemoryCacheTest {

    @Test
    fun testConstructor() {
        LruMemoryCache(maxSize = 10L * 1024 * 1024).apply {
            assertEquals("10MB", maxSize.formatFileSize())
            assertEquals("3MB", valueLimitedSize.formatFileSize())
        }

        LruMemoryCache(maxSize = 100L * 1024 * 1024).apply {
            assertEquals("100MB", maxSize.formatFileSize())
            assertEquals("30MB", valueLimitedSize.formatFileSize())
        }

        LruMemoryCache(maxSize = 100L * 1024 * 1024, valueLimitedSize = 80L * 1024 * 1024).apply {
            assertEquals("100MB", maxSize.formatFileSize())
            assertEquals("80MB", valueLimitedSize.formatFileSize())
        }
    }

    @Test
    fun testMaxSize() {
        LruMemoryCache(maxSize = 10L * 1024 * 1024).apply {
            assertEquals("10MB", maxSize.formatFileSize())
        }

        LruMemoryCache(maxSize = 100L * 1024 * 1024).apply {
            assertEquals("100MB", maxSize.formatFileSize())
        }
    }

    @Test
    fun testSize() {
        LruMemoryCache(maxSize = 10L * 1024 * 1024).apply {
            assertEquals("0B", size.formatFileSize())

            putBitmap("image1", 1)
            assertEquals("1MB", size.formatFileSize())

            putBitmap("image2", 2)
            assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGet() {
        LruMemoryCache(maxSize = 10L * 1024 * 1024).apply {
            assertNull(get("image1"))
            assertEquals(0, putBitmap("image1", 1))
            assertNotNull(get("image1"))
            assertEquals(-1, putBitmap("image1", 1))

            assertNull(get("image2"))
            putBitmap("image2", 2)
            assertNotNull(get("image1"))
            assertNotNull(get("image2"))

            remove("image1")
            assertNull(get("image1"))
            assertNotNull(get("image2"))

            remove("image2")
            assertNull(get("image1"))
            assertNull(get("image2"))
        }
    }

    @Test
    fun testLRU() {
        val sketch = getTestContextAndSketch().second
        LruMemoryCache(
            maxSize = 10L * 1024 * 1024,
            valueLimitedSize = (10L * 1024 * 1024 * 0.8f).roundToLong()
        ).apply {
            assertEquals("0B", size.formatFileSize())

            val bigBitmapSize = (sketch.memoryCache.maxSize.toFloat() / 1024 / 1024 * 0.8f).toInt()
            assertEquals(-2, putBitmap("image0", bigBitmapSize))
            assertEquals("0B", size.formatFileSize())

            assertEquals(0, putBitmap("image1", 1))
            assertEquals("1MB", size.formatFileSize())
            assertNotNull(get("image1"))

            assertEquals(0, putBitmap("image2", 2))
            assertEquals("3MB", size.formatFileSize())
            assertNotNull(get("image1"))
            assertNotNull(get("image2"))

            assertEquals(0, putBitmap("image3", 3))
            assertEquals("6MB", size.formatFileSize())
            assertNotNull(get("image1"))
            assertNotNull(get("image2"))
            assertNotNull(get("image3"))

            assertEquals(0, putBitmap("image4", 4))
            assertEquals("10MB", size.formatFileSize())
            assertNotNull(get("image1"))
            assertNotNull(get("image2"))
            assertNotNull(get("image3"))
            assertNotNull(get("image4"))

            assertEquals(0, putBitmap("image5", 5))
            assertEquals("9MB", size.formatFileSize())
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNull(get("image3"))
            assertNotNull(get("image4"))
            assertNotNull(get("image5"))

            assertEquals(0, putBitmap("image6", 6))
            assertEquals("6MB", size.formatFileSize())
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNull(get("image3"))
            assertNull(get("image4"))
            assertNull(get("image5"))
            assertNotNull(get("image6"))

            assertEquals(0, putBitmap("image7", 7))
            assertEquals("7MB", size.formatFileSize())
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNull(get("image3"))
            assertNull(get("image4"))
            assertNull(get("image5"))
            assertNull(get("image6"))
            assertNotNull(get("image7"))
        }
    }

    @Test
    fun testTrim() {
        LruMemoryCache(maxSize = 10L * 1024 * 1024, valueLimitedSize = 5L * 1024 * 1024).apply {
            assertEquals("0B", size.formatFileSize())
            putBitmap("image1", 1)
            putBitmap("image2", 2)
            putBitmap("image3", 3)
            putBitmap("image4", 4)
            assertNotNull(get("image1"))
            assertNotNull(get("image2"))
            assertNotNull(get("image3"))
            assertNotNull(get("image4"))
            assertEquals("10MB", size.formatFileSize())

            trim(7L * 1024 * 1024)
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNotNull(get("image3"))
            assertNotNull(get("image4"))
            assertEquals("7MB", size.formatFileSize())

            trim(4L * 1024 * 1024)
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNull(get("image3"))
            assertNotNull(get("image4"))
            assertEquals("4MB", size.formatFileSize())

            trim(0L)
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNull(get("image3"))
            assertNull(get("image4"))
            assertEquals("0B", size.formatFileSize())
        }
    }

    @Test
    fun testClear() {
        LruMemoryCache(maxSize = 10L * 1024 * 1024, valueLimitedSize = 5L * 1024 * 1024).apply {
            assertEquals("0B", size.formatFileSize())
            putBitmap("image1", 1)
            putBitmap("image2", 2)
            putBitmap("image3", 3)
            putBitmap("image4", 4)
            assertNotNull(get("image1"))
            assertNotNull(get("image2"))
            assertNotNull(get("image3"))
            assertNotNull(get("image4"))
            assertEquals("10MB", size.formatFileSize())

            clear()
            assertNull(get("image1"))
            assertNull(get("image2"))
            assertNull(get("image3"))
            assertNull(get("image4"))
            assertEquals("0B", size.formatFileSize())
        }
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

        val cache = LruMemoryCache(100L * 1024 * 1024)
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
        val element1 = LruMemoryCache(100)
        val element11 = LruMemoryCache(100)
        val element2 = LruMemoryCache(200)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        LruMemoryCache(10L * 1024 * 1024).apply {
            assertEquals("LruMemoryCache(maxSize=10MB,valueLimitedSize=3MB)", toString())
        }

        LruMemoryCache(100L * 1024 * 1024).apply {
            assertEquals("LruMemoryCache(maxSize=100MB,valueLimitedSize=30MB)", toString())
        }
    }

    private fun LruMemoryCache.putBitmap(imageUri: String, sizeMb: Int): Int {
        val bytes = sizeMb * 1024 * 1024
        val pixelCount = bytes / 4
        val width = 10
        val height = pixelCount / width
        val image = createBitmapImage(width, height)
        val newCacheValue = createCacheValue(
            image = image,
            extras = newCacheValueExtras(
                imageInfo = ImageInfo(width, height, "image/jpeg"),
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                transformeds = null,
                extras = null,
            )
        )
        return put(imageUri, newCacheValue)
    }
}