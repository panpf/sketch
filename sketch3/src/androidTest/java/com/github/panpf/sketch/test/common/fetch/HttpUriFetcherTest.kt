package com.github.panpf.sketch.test.common.fetch

import android.net.Uri
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.cache.CachePolicy
import com.github.panpf.sketch.common.datasource.ByteArrayDataSource
import com.github.panpf.sketch.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch.common.fetch.FetchResult
import com.github.panpf.sketch.common.fetch.HttpUriFetcher
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.test.internal.TestHttpStack
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class HttpUriFetcherTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val httpDownloadRequest = DownloadRequest.new("http://sample.com.sample.jpg")
        val httpsDownloadRequest = DownloadRequest.new("https://sample.com.sample.jpg")
        val ftpDownloadRequest = DownloadRequest.new("ftp://sample.com.sample.jpg")
        val contentDownloadRequest = DownloadRequest.new("content://sample.com.sample.jpg")
        val httpUriFetcherFactory = HttpUriFetcher.Factory()

        Assert.assertNotNull(httpUriFetcherFactory.create(sketch, httpDownloadRequest, null))
        Assert.assertNotNull(httpUriFetcherFactory.create(sketch, httpsDownloadRequest, null))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, ftpDownloadRequest, null))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, contentDownloadRequest, null))
    }

    @Test
    fun testFetchBlockingRepeatDownload() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val diskCache = sketch.diskCache
        val httpUriFetcherFactory = HttpUriFetcher.Factory()
        val testUri = TestHttpStack.urls.first()

        // Loop the test 50 times without making any mistakes
        repeat(50) {
            runBlocking {
                val request = DownloadRequest.new(testUri.uri)
                val httpUriFetcher = httpUriFetcherFactory.create(sketch, request, null)!!
                val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())

                diskCache[encodedDiskCacheKey]?.delete()
                Assert.assertNull(diskCache[encodedDiskCacheKey])

                val deferredList = mutableListOf<Deferred<FetchResult?>>()
                // Make 100 requests in a short period of time, expect only the first one to be downloaded from the network and the next 99 to be read from the disk cache
                repeat(100) {
                    val deferred = async {
                        httpUriFetcher.fetch()
                    }
                    deferredList.add(deferred)
                }
                val resultList = deferredList.map { it.await() }
                Assert.assertEquals(100, resultList.size)
                val fromNetworkList = resultList.mapIndexedNotNull { index, fetchResult ->
                    if (fetchResult!!.from == DataFrom.NETWORK) {
                        index to DataFrom.NETWORK
                    } else {
                        null
                    }
                }
                val fromDiskCacheList = resultList.mapIndexedNotNull { index, fetchResult ->
                    if (fetchResult!!.from == DataFrom.DISK_CACHE) {
                        index to DataFrom.DISK_CACHE
                    } else {
                        null
                    }
                }
                val message = buildString {
                    append("The results are as follows")
                    appendLine()
                    append(fromNetworkList.joinToString { "${it.first}:${it.second}" })
                    appendLine()
                    append(fromDiskCacheList.joinToString { "${it.first}:${it.second}" })
                }
                Assert.assertTrue(
                    message,
                    fromNetworkList.size == 1
                            && fromNetworkList.first().first == 0
                            && fromDiskCacheList.size == 99
                )
            }
        }
    }

    @Test
    fun testFetchByDiskCachePolicy() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val diskCache = sketch.diskCache
        val httpUriFetcherFactory = HttpUriFetcher.Factory()
        val testUri = TestHttpStack.urls.first()

        // CachePolicy.ENABLED
        runBlocking {
            val request = DownloadRequest.new(testUri.uri) {
                diskCachePolicy(CachePolicy.ENABLED)
            }
            val httpUriFetcher = httpUriFetcherFactory.create(sketch, request, null)!!
            val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())

            diskCache[encodedDiskCacheKey]?.delete()
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is DiskCacheDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNotNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.DISK_CACHE, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is DiskCacheDataSource && this.source.from == DataFrom.DISK_CACHE
                )
            }
            Assert.assertNotNull(diskCache[encodedDiskCacheKey])
        }

        // CachePolicy.DISABLED
        runBlocking {
            val request = DownloadRequest.new(testUri.uri) {
                diskCachePolicy(CachePolicy.DISABLED)
            }
            val httpUriFetcher = httpUriFetcherFactory.create(sketch, request, null)!!
            val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())

            diskCache[encodedDiskCacheKey]?.delete()
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is ByteArrayDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is ByteArrayDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNull(diskCache[encodedDiskCacheKey])
        }

        // CachePolicy.READ_ONLY
        runBlocking {
            val request = DownloadRequest.new(testUri.uri) {
                diskCachePolicy(CachePolicy.READ_ONLY)
            }
            val httpUriFetcher = httpUriFetcherFactory.create(sketch, request, null)!!
            val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())

            diskCache[encodedDiskCacheKey]?.delete()
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is ByteArrayDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is ByteArrayDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            val request2 = DownloadRequest.new(testUri.uri) {
                diskCachePolicy(CachePolicy.ENABLED)
            }
            val httpUriFetcher2 = httpUriFetcherFactory.create(sketch, request2, null)!!
            httpUriFetcher2.fetch()
            Assert.assertNotNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.DISK_CACHE, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is DiskCacheDataSource && this.source.from == DataFrom.DISK_CACHE
                )
            }
            Assert.assertNotNull(diskCache[encodedDiskCacheKey])
        }

        // CachePolicy.WRITE_ONLY
        runBlocking {
            val request = DownloadRequest.new(testUri.uri) {
                diskCachePolicy(CachePolicy.WRITE_ONLY)
            }
            val httpUriFetcher = httpUriFetcherFactory.create(sketch, request, null)!!
            val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())

            diskCache[encodedDiskCacheKey]?.delete()
            Assert.assertNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is ByteArrayDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNotNull(diskCache[encodedDiskCacheKey])

            httpUriFetcher.fetch()!!.apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.from)
                Assert.assertTrue(
                    this.toString(),
                    this.source is ByteArrayDataSource && this.source.from == DataFrom.NETWORK
                )
            }
            Assert.assertNotNull(diskCache[encodedDiskCacheKey])
        }
    }

    @Test
    fun testProgress() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val diskCache = sketch.diskCache
        val httpUriFetcherFactory = HttpUriFetcher.Factory()
        val testUri = TestHttpStack.urls.first()

        val progressList = mutableListOf<Long>()
        val request = DownloadRequest.new(testUri.uri)
        val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())

        diskCache[encodedDiskCacheKey]?.delete()
        Assert.assertNull(diskCache[encodedDiskCacheKey])

        val httpUriFetcher =
            httpUriFetcherFactory.create(sketch, request) { _, _, completedLength ->
                progressList.add(completedLength)
            }!!
        runBlocking {
            httpUriFetcher.fetch()
            delay(1000)
        }
        Assert.assertTrue(progressList.size > 0)
        Assert.assertEquals(testUri.contentLength, progressList.last())

        var lastProgress: Long? = null
        progressList.forEach { progress ->
            val currentLastProgress = lastProgress
            if (currentLastProgress != null) {
                Assert.assertTrue(currentLastProgress < progress)
            }
            lastProgress = progress
        }
    }

    @Test
    fun testCancel() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }
        val diskCache = sketch.diskCache
        val httpUriFetcherFactory = HttpUriFetcher.Factory()
        val testUri = TestHttpStack.urls.first()
        val progressList = mutableListOf<Long>()
        val request = DownloadRequest.new(testUri.uri)
        val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())
        val httpUriFetcher =
            httpUriFetcherFactory.create(sketch, request) { _, _, completedLength ->
                progressList.add(completedLength)
            }!!

        diskCache[encodedDiskCacheKey]?.delete()
        Assert.assertNull(diskCache[encodedDiskCacheKey])

        progressList.clear()
        runBlocking {
            val job = launch {
                httpUriFetcher.fetch()
            }
            delay(2000)
            job.cancel()
            val repeatTaskFilter = sketch.repeatTaskFilter
            Assert.assertNotNull(repeatTaskFilter.getHttpFetchTaskDeferred(testUri.uri.toString()))
            Assert.assertNotNull(diskCache.getEdiTaskDeferred(encodedDiskCacheKey))
            delay(1000)
            Assert.assertNull(repeatTaskFilter.getHttpFetchTaskDeferred(testUri.uri.toString()))
            Assert.assertNull(diskCache.getEdiTaskDeferred(encodedDiskCacheKey))
        }
        Assert.assertTrue(progressList.size > 0)
        Assert.assertNull(progressList.find { it == testUri.contentLength })
    }

    @Test
    fun testException() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }
        val diskCache = sketch.diskCache
        val httpUriFetcherFactory = HttpUriFetcher.Factory()
        val testUri = TestHttpStack.TestUri(Uri.parse("http://fake.jpeg"), 43235)
        val progressList = mutableListOf<Long>()
        val request = DownloadRequest.new(testUri.uri)
        val encodedDiskCacheKey = diskCache.encodeKey(request.uri.toString())
        val httpUriFetcher =
            httpUriFetcherFactory.create(sketch, request) { _, _, completedLength ->
                progressList.add(completedLength)
            }!!

        diskCache[encodedDiskCacheKey]?.delete()
        Assert.assertNull(diskCache[encodedDiskCacheKey])

        progressList.clear()
        runBlocking {
            try {
                httpUriFetcher.fetch()
                Assert.fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val repeatTaskFilter = sketch.repeatTaskFilter
            Assert.assertNull(repeatTaskFilter.getHttpFetchTaskDeferred(testUri.uri.toString()))
            Assert.assertNull(diskCache.getEdiTaskDeferred(encodedDiskCacheKey))
        }
        Assert.assertEquals(0, progressList.size)
    }
}