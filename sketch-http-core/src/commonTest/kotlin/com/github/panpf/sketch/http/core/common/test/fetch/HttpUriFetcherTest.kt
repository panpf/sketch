package com.github.panpf.sketch.http.core.common.test.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.createImageSerializer
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.isHttpUri
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MyCacheKeyMapper
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okio.IOException
import okio.buffer
import okio.use
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
        HttpUriFetcher(sketch, httpStack, request, "")
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

                val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
                val downloadCache = sketch.downloadCache
                downloadCache.remove(downloadCacheKey)
                assertFalse(downloadCache.exist(downloadCacheKey))

                val deferredList = mutableListOf<Deferred<FetchResult?>>()
                // Make 100 requests in a short period of time, expect only the first one to be downloaded from the network and the next 99 to be read from the disk cache
                repeat(100) {
                    val deferred = async(ioCoroutineDispatcher()) {
                        HttpUriFetcher(
                            sketch,
                            TestHttpStack(context),
                            request,
                            downloadCacheKey
                        ).fetch().getOrNull()
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
                val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
                val httpUriFetcher =
                    HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey)

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
                val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
                val httpUriFetcher =
                    HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey)

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
                val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
                val httpUriFetcher =
                    HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey)

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
                val downloadCacheKey2 = request2.toRequestContext(sketch).downloadCacheKey
                val httpUriFetcher2 =
                    HttpUriFetcher(sketch, TestHttpStack(context), request2, downloadCacheKey2)
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
                val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
                val httpUriFetcher =
                    HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey)

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
    fun testResultCacheKey() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val downloadCache = sketch.downloadCache
        val testUri = TestHttpStack.testImages.first()
        val executeRequest: suspend (ImageRequest) -> FetchResult? = { request ->
            val requestContext = request.toRequestContext(sketch)
            HttpUriFetcher(
                sketch,
                TestHttpStack(context),
                request,
                requestContext.downloadCacheKey
            ).fetch().getOrNull()
        }

        downloadCache.clear()
        assertEquals(expected = 0, actual = downloadCache.size)

        val editor = downloadCache.openEditor(key = "downloadCacheKey1")!!
        downloadCache.fileSystem.sink(editor.data).buffer().use {
            val bitmapImage = createBitmapImage(100, 100)
            createImageSerializer().compress(bitmapImage, it)
        }
        downloadCache.fileSystem.sink(editor.metadata).buffer().use {
            val metadata = ResultCacheDecodeInterceptor.Metadata(
                imageInfo = ImageInfo(width = 100, height = 100, mimeType = "image/png"),
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                transformeds = null,
                extras = null
            )
            it.writeUtf8(metadata.toMetadataString())
        }
        editor.commit()
        assertEquals(expected = 273, actual = downloadCache.size)

        executeRequest(ImageRequest(context, testUri.uri) {
            downloadCachePolicy(ENABLED)
            depth(Depth.LOCAL)
        }).apply {
            assertNull(this)
        }

        executeRequest(ImageRequest(context, testUri.uri) {
            downloadCachePolicy(ENABLED)
            depth(Depth.LOCAL)
            downloadCacheKey("downloadCacheKey1")
        }).apply {
            assertNotNull(this)
        }

        executeRequest(ImageRequest(context, testUri.uri) {
            downloadCachePolicy(ENABLED)
            depth(Depth.LOCAL)
            downloadCacheKeyMapper(MyCacheKeyMapper("downloadCacheKey1"))
        }).apply {
            assertNotNull(this)
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
            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            val httpUriFetcher =
                HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey)
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

            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            val downloadCache = sketch.downloadCache
            downloadCache.remove(downloadCacheKey)
            assertFalse(downloadCache.exist(downloadCacheKey))

            progressList.clear()
            val job = launch(ioCoroutineDispatcher()) {
                val httpStack = TestHttpStack(context, readDelayMillis = 500)
                val httpUriFetcher = HttpUriFetcher(sketch, httpStack, request, downloadCacheKey)
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

            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
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
                val httpUriFetcher = HttpUriFetcher(sketch, httpStack, request, downloadCacheKey)
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
            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey).fetch()
                    .getOrThrow()
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
            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey).fetch()
                    .getOrThrow()
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
            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            sketch.downloadCache.clear()

            HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey).fetch()
                .getOrThrow()
            assertTrue(progressList.isEmpty())
            assertNull(progressList.find { it == testUri.contentLength })

            assertTrue(sketch.downloadCache.exist(downloadCacheKey))
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
            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey).fetch()
                    .getOrThrow()
                fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            block(1000)
            assertTrue(progressList.isNotEmpty())
            assertNotNull(progressList.find { it == testUri.contentLength + 1 })

            assertFalse(sketch.downloadCache.exist(downloadCacheKey))
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
            val downloadCacheKey = request.toRequestContext(sketch).downloadCacheKey
            sketch.downloadCache.clear()
            try {
                HttpUriFetcher(sketch, TestHttpStack(context), request, downloadCacheKey).fetch()
                    .getOrThrow()
                fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            block(1000)
            assertTrue(progressList.size > 0)
            assertNotNull(progressList.find { it == testUri.contentLength + 1 })

            assertFalse(sketch.downloadCache.exist(downloadCacheKey))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val (context, sketch) = getTestContextAndSketch()
        val sketch2 = Sketch(context)
        val httpStack = TestHttpStack(context)
        val httpStack2 = TestHttpStack(context, readDelayMillis = 3000)
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val request2 = request.newRequest { memoryCachePolicy(DISABLED) }
        val element1 = HttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
        val element11 = HttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
        val element2 = HttpUriFetcher(sketch2, httpStack, request, "downloadCacheKey")
        val element3 = HttpUriFetcher(sketch, httpStack2, request, "downloadCacheKey")
        val element4 = HttpUriFetcher(sketch, httpStack, request2, "downloadCacheKey")
        val element5 = HttpUriFetcher(sketch, httpStack, request, "downloadCacheKey2")

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val httpStack = TestHttpStack(context)
        val httpUriFetcher = HttpUriFetcher(sketch, httpStack, request, "downloadCacheKey2")
        assertEquals(
            expected = "HttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request, downloadCacheKey='downloadCacheKey2')",
            actual = httpUriFetcher.toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val httpUri = "http://sample.com/sample.jpg"
        val httpsUri = "https://sample.com/sample.jpg"
        val ftpUri = "ftp://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val testHttpStack = TestHttpStack(context)
        val factory = HttpUriFetcher.Factory(testHttpStack)
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
        val context = getTestContext()
        val testHttpStack = TestHttpStack(context)
        val element1 = HttpUriFetcher.Factory(testHttpStack)
        val element11 = HttpUriFetcher.Factory(testHttpStack)

        assertEquals(element1, element11)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        val context = getTestContext()
        val testHttpStack = TestHttpStack(context)
        assertEquals(
            expected = "HttpUriFetcher(httpStack=$testHttpStack)",
            actual = HttpUriFetcher.Factory(testHttpStack).toString()
        )
    }
}