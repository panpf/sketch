package com.github.panpf.sketch.http.core.common.test.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.downloadCacheKey
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.isHttpUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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
        assertTrue(isHttpUri("http://sample.com/sample.jpg".toUri()))
        assertTrue(isHttpUri("https://sample.com/sample.jpg".toUri()))
        assertFalse(isHttpUri("ftp://sample.com/sample.jpg".toUri()))
        assertFalse(isHttpUri("content://sample_app/sample".toUri()))
    }

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val httpStack = TestHttpStack(context)
        HttpUriFetcher(sketch, httpStack, request)
    }

    @Test
    fun testCompanion() {
        assertEquals("http", HttpUriFetcher.SCHEME_HTTP)
        assertEquals("https", HttpUriFetcher.SCHEME_HTTPS)
        assertEquals("text/plain", HttpUriFetcher.MIME_TYPE_TEXT_PLAIN)
    }

    @Test
    fun testRepeatDownload() = runTest {
        runInNewSketchWithUse { context, sketch ->
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
                        HttpUriFetcher(sketch, TestHttpStack(context), request).fetch().getOrNull()
                    }
                    deferredList.add(deferred)
                }
                val resultList = deferredList.map { it.await() }
                assertEquals(100, resultList.size)
                val fromNetworkList = resultList.mapIndexedNotNull { index, fetchResult ->
                    if (fetchResult!!.dataFrom == NETWORK) {
                        index to NETWORK
                    } else {
                        null
                    }
                }
                val fromDiskCacheList = resultList.mapIndexedNotNull { index, fetchResult ->
                    if (fetchResult!!.dataFrom == DOWNLOAD_CACHE) {
                        index to DOWNLOAD_CACHE
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
    }

    @Test
    fun testDiskCachePolicy() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val testUri = TestHttpStack.testImages.first()

            // CachePolicy.ENABLED
            runBlock {
                val request = ImageRequest(context, testUri.uri) {
                    downloadCachePolicy(ENABLED)
                }
                val httpUriFetcher = HttpUriFetcher(sketch, TestHttpStack(context), request)

                val downloadCacheKey = request.downloadCacheKey
                val downloadCache = sketch.downloadCache
                downloadCache.remove(downloadCacheKey)
                assertFalse(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is FileDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertTrue(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(DOWNLOAD_CACHE, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is FileDataSource && this.dataSource.dataFrom == DOWNLOAD_CACHE,
                        this.toString(),
                    )
                }
                assertTrue(downloadCache.exist(downloadCacheKey))
            }

            // CachePolicy.DISABLED
            runBlock {
                val request = ImageRequest(context, testUri.uri) {
                    downloadCachePolicy(DISABLED)
                }
                val httpUriFetcher = HttpUriFetcher(sketch, TestHttpStack(context), request)

                val downloadCacheKey = request.downloadCacheKey
                val downloadCache = sketch.downloadCache
                downloadCache.remove(downloadCacheKey)
                assertFalse(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertFalse(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertFalse(downloadCache.exist(downloadCacheKey))
            }

            // CachePolicy.READ_ONLY
            runBlock {
                val request = ImageRequest(context, testUri.uri) {
                    downloadCachePolicy(READ_ONLY)
                }
                val httpUriFetcher = HttpUriFetcher(sketch, TestHttpStack(context), request)

                val downloadCacheKey = request.downloadCacheKey
                val downloadCache = sketch.downloadCache
                downloadCache.remove(downloadCacheKey)
                assertFalse(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertFalse(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertFalse(downloadCache.exist(downloadCacheKey))

                val request2 = ImageRequest(context, testUri.uri) {
                    downloadCachePolicy(ENABLED)
                }
                val httpUriFetcher2 = HttpUriFetcher(sketch, TestHttpStack(context), request2)
                httpUriFetcher2.fetch().getOrThrow()
                assertTrue(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(DOWNLOAD_CACHE, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is FileDataSource && this.dataSource.dataFrom == DOWNLOAD_CACHE,
                        this.toString(),
                    )
                }
                assertTrue(downloadCache.exist(downloadCacheKey))
            }

            // CachePolicy.WRITE_ONLY
            runBlock {
                val request = ImageRequest(context, testUri.uri) {
                    downloadCachePolicy(WRITE_ONLY)
                }
                val httpUriFetcher = HttpUriFetcher(sketch, TestHttpStack(context), request)

                val downloadCacheKey = request.downloadCacheKey
                val downloadCache = sketch.downloadCache
                downloadCache.remove(downloadCacheKey)
                assertFalse(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertTrue(downloadCache.exist(downloadCacheKey))

                httpUriFetcher.fetch().getOrThrow().apply {
                    assertEquals(NETWORK, this.dataFrom, this.toString())
                    assertTrue(
                        this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == NETWORK,
                        this.toString(),
                    )
                }
                assertTrue(downloadCache.exist(downloadCacheKey))
            }
        }
    }

    @Test
    fun testProgress() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val testUri = TestHttpStack.testImages.first()
            val progressList = mutableListOf<Long>()
            val request = ImageRequest(context, testUri.uri) {
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }

            val downloadCache = sketch.downloadCache
            val downloadCacheKey = request.downloadCacheKey
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            val httpUriFetcher = HttpUriFetcher(sketch, TestHttpStack(context), request)
            httpUriFetcher.fetch().getOrThrow()
            block(1000)
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
    }

    @Test
    fun testCancel() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val testUri = TestHttpStack.testImages.first()
            val progressList = mutableListOf<Long>()
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(DISABLED)
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            progressList.clear()
            val job = launch(ioCoroutineDispatcher()) {
                val httpStack = TestHttpStack(context, readDelayMillis = 500)
                val httpUriFetcher = HttpUriFetcher(sketch, httpStack, request)
                httpUriFetcher.fetch().getOrThrow()
            }
            block(2000)
            job.cancel()
            assertTrue(progressList.size > 0)
            assertNull(progressList.find { it == testUri.contentLength })
        }
    }

    @Test
    fun testCancel2() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val testUri = TestHttpStack.testImages.first()
            val progressList = mutableListOf<Long>()
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(DISABLED)
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }

            val downloadCacheKey = request.downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            progressList.clear()
            val job = launch(ioCoroutineDispatcher()) {
                val httpStack = TestHttpStack(
                    context = context,
                    readDelayMillis = 500,
                    connectionDelayMillis = 500
                )
                val httpUriFetcher = HttpUriFetcher(sketch, httpStack, request)
                httpUriFetcher.fetch().getOrThrow()
            }
            block(500)
            job.cancel()
            assertTrue(progressList.size == 0)
        }
    }

    @Test
    fun testErrorUrl() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val progressList = mutableListOf<Long>()
            val request = ImageRequest(context, "http://error.com/sample.jpeg") {
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request).fetch().getOrThrow()
                fail("No exception thrown")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            assertEquals(0, progressList.size)
        }
    }

    @Test
    fun testContentUrl() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val progressList = mutableListOf<Long>()
            val request = ImageRequest(context, TestHttpStack.errorImage.uri) {
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request).fetch().getOrThrow()
                fail("No exception thrown")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            assertEquals(0, progressList.size)
        }
    }

    @Test
    fun testChunkedImage() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val progressList = mutableListOf<Long>()
            val testUri = TestHttpStack.chunkedErrorImage
            val request = ImageRequest(context, testUri.uri) {
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }
            sketch.downloadCache.clear()

            HttpUriFetcher(sketch, TestHttpStack(context), request).fetch().getOrThrow()
            assertTrue(progressList.size == 0)
            assertNull(progressList.find { it == testUri.contentLength })

            assertTrue(sketch.downloadCache.exist(request.downloadCacheKey))
        }
    }

    @Test
    fun testLengthError() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val progressList = mutableListOf<Long>()
            val testUri = TestHttpStack.lengthErrorImage
            val request = ImageRequest(context, testUri.uri) {
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request).fetch().getOrThrow()
                fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            block(1000)
            assertTrue(progressList.size > 0)
            assertNotNull(progressList.find { it == testUri.contentLength + 1 })

            assertFalse(sketch.downloadCache.exist(request.downloadCacheKey))
        }
    }

    @Test
    fun testLengthError2() = runTest {
        runInNewSketchWithUse { context, sketch ->
            val progressList = mutableListOf<Long>()
            val testUri = TestHttpStack.lengthErrorImage
            val request = ImageRequest(context, testUri.uri) {
                downloadCachePolicy(DISABLED)
                addProgressListener { _, progress ->
                    progressList.add(progress.completedLength)
                }
            }
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request).fetch().getOrThrow()
                fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            block(1000)
            assertTrue(progressList.size > 0)
            assertNotNull(progressList.find { it == testUri.contentLength + 1 })

            assertFalse(sketch.downloadCache.exist(request.downloadCacheKey))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val (context, sketch) = getTestContextAndSketch()
        val sketch2 = Sketch.Builder(context).build()
        val httpStack = TestHttpStack(context)
        val httpStack2 = TestHttpStack(context, readDelayMillis = 3000)
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val request2 = request.newRequest { memoryCachePolicy(DISABLED) }
        val element1 = HttpUriFetcher(sketch, httpStack, request)
        val element11 = HttpUriFetcher(sketch, httpStack, request)
        val element2 = HttpUriFetcher(sketch2, httpStack, request)
        val element3 = HttpUriFetcher(sketch, httpStack2, request)
        val element4 = HttpUriFetcher(sketch, httpStack, request2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val httpStack = TestHttpStack(context)
        val httpUriFetcher = HttpUriFetcher(sketch, httpStack, request)
        assertEquals(
            expected = "HttpUriFetcher('http://sample.com/sample.jpg')",
            actual = httpUriFetcher.toString()
        )
    }
}