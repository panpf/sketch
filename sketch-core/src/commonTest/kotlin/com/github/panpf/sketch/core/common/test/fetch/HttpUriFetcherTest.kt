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

package com.github.panpf.sketch.core.common.test.fetch

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.downloadCacheKey
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okio.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class HttpUriFetcherTest {

    @Test
    fun testIsHttpUri() {
        // TODO test
    }

    @Test
    fun testConstructor() {
        // TODO test
    }

    @Test
    fun testCompanion() {
        // TODO test
    }

    @Test
    fun testRepeatDownload() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        // Loop the test 50 times without making any mistakes
        val testUri = TestHttpStack.testImages.first()
        repeat(50) {
            val request = ImageRequest(context, testUri.uri)

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            val deferredList = mutableListOf<Deferred<FetchResult?>>()
            // Make 100 requests in a short period of time, expect only the first one to be downloaded from the network and the next 99 to be read from the disk cache
            repeat(100) {
                val deferred = async(ioCoroutineDispatcher()) {
                    HttpUriFetcher.Factory().create(
                        request
                            .toRequestContext(sketch, Size.Empty)
                    )!!.fetch().getOrNull()
                }
                deferredList.add(deferred)
            }
            val resultList = deferredList.map { it.await() }
            assertEquals(100, resultList.size)
            val fromNetworkList = resultList.mapIndexedNotNull { index, fetchResult ->
                if (fetchResult!!.dataFrom == DataFrom.NETWORK) {
                    index to DataFrom.NETWORK
                } else {
                    null
                }
            }
            val fromDiskCacheList = resultList.mapIndexedNotNull { index, fetchResult ->
                if (fetchResult!!.dataFrom == DataFrom.DOWNLOAD_CACHE) {
                    index to DataFrom.DOWNLOAD_CACHE
                } else {
                    null
                }
            }
            assertTrue(
                fromNetworkList.size == 1 && fromDiskCacheList.size == 99,
                "The results are as follows\n${fromNetworkList.joinToString { "${it.first}:${it.second}" }}\n${fromDiskCacheList.joinToString { "${it.first}:${it.second}" }}",
            )
        }
    }

    @Test
    fun testDiskCachePolicy() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val testUri = TestHttpStack.testImages.first()

        // CachePolicy.ENABLED
        runBlock {
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(CachePolicy.ENABLED)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is FileDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertTrue(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.DOWNLOAD_CACHE, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is FileDataSource && this.dataSource.dataFrom == DataFrom.DOWNLOAD_CACHE,
                    this.toString(),
                )
            }
            assertTrue(downloadCache.exist(downloadCacheKey))
        }

        // CachePolicy.DISABLED
        runBlock {
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(CachePolicy.DISABLED)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertFalse(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertFalse(downloadCache.exist(downloadCacheKey))
        }

        // CachePolicy.READ_ONLY
        runBlock {
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(CachePolicy.READ_ONLY)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertFalse(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertFalse(downloadCache.exist(downloadCacheKey))

            val request2 = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(CachePolicy.ENABLED)
            }
            val httpUriFetcher2 = HttpUriFetcher.Factory().create(
                request2
                    .toRequestContext(sketch, Size.Empty)
            )!!
            httpUriFetcher2.fetch().getOrThrow()
            assertTrue(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.DOWNLOAD_CACHE, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is FileDataSource && this.dataSource.dataFrom == DataFrom.DOWNLOAD_CACHE,
                    this.toString(),
                )
            }
            assertTrue(downloadCache.exist(downloadCacheKey))
        }

        // CachePolicy.WRITE_ONLY
        runBlock {
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(CachePolicy.WRITE_ONLY)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertTrue(downloadCache.exist(downloadCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                assertEquals(DataFrom.NETWORK, this.dataFrom, this.toString())
                assertTrue(
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK,
                    this.toString(),
                )
            }
            assertTrue(downloadCache.exist(downloadCacheKey))
        }
    }

    @Test
    fun testProgress() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val testUri = TestHttpStack.testImages.first()
        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, testUri.uri) {
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }

        val downloadCache = sketch.downloadCache
        val downloadCacheKey = request.downloadCacheKey
        downloadCache.remove(downloadCacheKey)
        assertFalse(downloadCache.exist(downloadCacheKey))

        HttpUriFetcher.Factory().create(
            request
                .toRequestContext(sketch, Size.Empty)
        )!!.fetch().getOrThrow()
        delay(1000)
        assertTrue(progressList.size > 0)
        assertEquals(testUri.contentLength, progressList.last())

        var lastProgress: Long? = null
        progressList.forEach { progress ->
            val currentLastProgress = lastProgress
            if (currentLastProgress != null) {
                assertTrue(currentLastProgress < progress)
            }
            lastProgress = progress
        }
    }

    @Test
    fun testCancel() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it, readDelayMillis = 500))
        }

        val testUri = TestHttpStack.testImages.first()
        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, testUri.uri) {
            downloadCachePolicy(CachePolicy.DISABLED)
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }

        val downloadCacheKey = request.downloadCacheKey
        val downloadCache = sketch.downloadCache
        downloadCache.remove(downloadCacheKey)
        assertFalse(downloadCache.exist(downloadCacheKey))

        progressList.clear()
        val job = launch(ioCoroutineDispatcher()) {
            HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!.fetch().getOrThrow()
        }
        block(2000)
        job.cancel()
        assertTrue(progressList.size > 0)
        assertNull(progressList.find { it == testUri.contentLength })
    }

    @Test
    fun testCancel2() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it, readDelayMillis = 500, connectionDelayMillis = 500))
        }

        val testUri = TestHttpStack.testImages.first()
        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, testUri.uri) {
            downloadCachePolicy(CachePolicy.DISABLED)
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }

        val downloadCacheKey = request.downloadCacheKey
        val downloadCache = sketch.downloadCache
        downloadCache.remove(downloadCacheKey)
        assertFalse(downloadCache.exist(downloadCacheKey))

        progressList.clear()
        val job = launch(ioCoroutineDispatcher()) {
            HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!.fetch().getOrThrow()
        }
        block(500)
        job.cancel()
        assertTrue(progressList.size == 0)
    }

    @Test
    fun testErrorUrl() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, "http://error.com/sample.jpeg") {
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        try {
            HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!.fetch().getOrThrow()
            fail("No exception thrown")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        assertEquals(0, progressList.size)
    }

    @Test
    fun testContentUrl() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, TestHttpStack.errorImage.uri) {
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        try {
            HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!.fetch().getOrThrow()
            fail("No exception thrown")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        assertEquals(0, progressList.size)
    }

    @Test
    fun testChunkedImage() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val testUri = TestHttpStack.chunkedErrorImage
        val request = ImageRequest(context, testUri.uri) {
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()

        HttpUriFetcher.Factory().create(
            request
                .toRequestContext(sketch, Size.Empty)
        )!!.fetch().getOrThrow()
        assertTrue(progressList.size == 0)
        assertNull(progressList.find { it == testUri.contentLength })

        assertTrue(sketch.downloadCache.exist(request.downloadCacheKey))
    }

    @Test
    fun testLengthError() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val testUri = TestHttpStack.lengthErrorImage
        val request = ImageRequest(context, testUri.uri) {
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        try {
            HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!.fetch().getOrThrow()
            fail("No exception thrown")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        delay(1000)
        assertTrue(progressList.size > 0)
        assertNotNull(progressList.find { it == testUri.contentLength + 1 })

        assertFalse(sketch.downloadCache.exist(request.downloadCacheKey))
    }

    @Test
    fun testLengthError2() = runTest {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val testUri = TestHttpStack.lengthErrorImage
        val request = ImageRequest(context, testUri.uri) {
            downloadCachePolicy(CachePolicy.DISABLED)
            registerProgressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        try {
            HttpUriFetcher.Factory().create(
                request
                    .toRequestContext(sketch, Size.Empty)
            )!!.fetch().getOrThrow()
            fail("No exception thrown")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        delay(1000)
        assertTrue(progressList.size > 0)
        assertNotNull(progressList.find { it == testUri.contentLength + 1 })

        assertFalse(sketch.downloadCache.exist(request.downloadCacheKey))
    }

    @Test
    fun testEqualsAndHashCode() {
        // TODO test
    }

    @Test
    fun testToString() {
        // TODO test
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val httpUri = "http://sample.com/sample.jpg"
        val httpsUri = "https://sample.com/sample.jpg"
        val ftpUri = "ftp://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val factory = HttpUriFetcher.Factory()
        assertNotNull(
            factory.create(
                ImageRequest(context, httpsUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNotNull(
            factory.create(
                ImageRequest(context, httpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            factory.create(
                ImageRequest(context, ftpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            factory.create(
                ImageRequest(context, contentUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = HttpUriFetcher.Factory()
        val element11 = HttpUriFetcher.Factory()

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        // TODO test
    }
}