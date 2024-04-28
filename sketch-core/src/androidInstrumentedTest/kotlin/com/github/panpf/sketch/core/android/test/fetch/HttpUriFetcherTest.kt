/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.core.android.test.fetch

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.exist
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class HttpUriFetcherTest {

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val httpUri = "http://sample.com/sample.jpg"
        val httpsUri = "https://sample.com/sample.jpg"
        val ftpUri = "ftp://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"
        val imageView = ImageView(context)

        val httpUriFetcherFactory = HttpUriFetcher.Factory()
        Assert.assertNotNull(
            httpUriFetcherFactory.create(
                sketch,
                ImageRequest(context, httpUri)
            )
        )
        Assert.assertNotNull(
            httpUriFetcherFactory.create(
                sketch,
                ImageRequest(context, httpsUri)
            )
        )
        Assert.assertNotNull(httpUriFetcherFactory.create(sketch, ImageRequest(context, httpUri)))
        Assert.assertNotNull(httpUriFetcherFactory.create(sketch, ImageRequest(context, httpsUri)))
        Assert.assertNotNull(
            httpUriFetcherFactory.create(
                sketch,
                ImageRequest(imageView, httpUri)
            )
        )
        Assert.assertNotNull(
            httpUriFetcherFactory.create(
                sketch,
                ImageRequest(imageView, httpsUri)
            )
        )
        Assert.assertNull(httpUriFetcherFactory.create(sketch, ImageRequest(context, ftpUri)))
        Assert.assertNull(
            httpUriFetcherFactory.create(
                sketch,
                ImageRequest(context, contentUri)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = HttpUriFetcher.Factory()
        val element11 = HttpUriFetcher.Factory()

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)

        Assert.assertNotEquals(element1, Any())
        Assert.assertNotEquals(element1, null)

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testRepeatDownload() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        // Loop the test 50 times without making any mistakes
        val testUri = TestHttpStack.testImages.first()
        repeat(50) {
            runBlocking {
                val request = ImageRequest(context, testUri.uriString)

                val diskCacheKey = request.uriString
                val diskCache = sketch.downloadCache
                diskCache.remove(diskCacheKey)
                Assert.assertFalse(diskCache.exist(diskCacheKey))

                val deferredList = mutableListOf<Deferred<FetchResult?>>()
                // Make 100 requests in a short period of time, expect only the first one to be downloaded from the network and the next 99 to be read from the disk cache
                repeat(100) {
                    val deferred = async(Dispatchers.IO) {
                        HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrNull()
                    }
                    deferredList.add(deferred)
                }
                val resultList = deferredList.map { it.await() }
                Assert.assertEquals(100, resultList.size)
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
                Assert.assertTrue(
                    "The results are as follows\n${fromNetworkList.joinToString { "${it.first}:${it.second}" }}\n${fromDiskCacheList.joinToString { "${it.first}:${it.second}" }}",
                    fromNetworkList.size == 1 && fromDiskCacheList.size == 99
                )
            }
        }
    }

    @Test
    fun testDiskCachePolicy() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val testUri = TestHttpStack.testImages.first()

        // CachePolicy.ENABLED
        runBlocking {
            val request = ImageRequest(context, testUri.uriString) {
                downloadCachePolicy(CachePolicy.ENABLED)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(sketch, request)!!

            val diskCacheKey = request.uriString
            val contentTypeDiskCacheKey = request.uriString + "_contentType"
            val diskCache = sketch.downloadCache
            diskCache.remove(diskCacheKey)
            diskCache.remove(contentTypeDiskCacheKey)
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is FileDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertTrue(diskCache.exist(diskCacheKey))
            Assert.assertTrue(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.DOWNLOAD_CACHE, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is FileDataSource && this.dataSource.dataFrom == DataFrom.DOWNLOAD_CACHE
                )
            }
            Assert.assertTrue(diskCache.exist(diskCacheKey))
            Assert.assertTrue(diskCache.exist(contentTypeDiskCacheKey))
        }

        // CachePolicy.DISABLED
        runBlocking {
            val request = ImageRequest(context, testUri.uriString) {
                downloadCachePolicy(DISABLED)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(sketch, request)!!

            val diskCacheKey = request.uriString
            val contentTypeDiskCacheKey = request.uriString + "_contentType"
            val diskCache = sketch.downloadCache
            diskCache.remove(diskCacheKey)
            diskCache.remove(contentTypeDiskCacheKey)
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))
        }

        // CachePolicy.READ_ONLY
        runBlocking {
            val request = ImageRequest(context, testUri.uriString) {
                downloadCachePolicy(CachePolicy.READ_ONLY)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(sketch, request)!!

            val diskCacheKey = request.uriString
            val contentTypeDiskCacheKey = request.uriString + "_contentType"
            val diskCache = sketch.downloadCache
            diskCache.remove(diskCacheKey)
            diskCache.remove(contentTypeDiskCacheKey)
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            val request2 = ImageRequest(context, testUri.uriString) {
                downloadCachePolicy(CachePolicy.ENABLED)
            }
            val httpUriFetcher2 = HttpUriFetcher.Factory().create(sketch, request2)!!
            httpUriFetcher2.fetch().getOrThrow()
            Assert.assertTrue(diskCache.exist(diskCacheKey))
            Assert.assertTrue(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.DOWNLOAD_CACHE, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is FileDataSource && this.dataSource.dataFrom == DataFrom.DOWNLOAD_CACHE
                )
            }
            Assert.assertTrue(diskCache.exist(diskCacheKey))
            Assert.assertTrue(diskCache.exist(contentTypeDiskCacheKey))
        }

        // CachePolicy.WRITE_ONLY
        runBlocking {
            val request = ImageRequest(context, testUri.uriString) {
                downloadCachePolicy(CachePolicy.WRITE_ONLY)
            }
            val httpUriFetcher = HttpUriFetcher.Factory().create(sketch, request)!!

            val diskCacheKey = request.uriString
            val contentTypeDiskCacheKey = request.uriString + "_contentType"
            val diskCache = sketch.downloadCache
            diskCache.remove(diskCacheKey)
            diskCache.remove(contentTypeDiskCacheKey)
            Assert.assertFalse(diskCache.exist(diskCacheKey))
            Assert.assertFalse(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertTrue(diskCache.exist(diskCacheKey))
            Assert.assertTrue(diskCache.exist(contentTypeDiskCacheKey))

            httpUriFetcher.fetch().getOrThrow().apply {
                Assert.assertEquals(this.toString(), DataFrom.NETWORK, this.dataFrom)
                Assert.assertTrue(
                    this.toString(),
                    this.dataSource is ByteArrayDataSource && this.dataSource.dataFrom == DataFrom.NETWORK
                )
            }
            Assert.assertTrue(diskCache.exist(diskCacheKey))
            Assert.assertTrue(diskCache.exist(contentTypeDiskCacheKey))
        }
    }

    @Test
    fun testProgress() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val testUri = TestHttpStack.testImages.first()
        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, testUri.uriString) {
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }

        val diskCache = sketch.downloadCache
        val diskCacheKey = request.uriString
        diskCache.remove(diskCacheKey)
        Assert.assertFalse(diskCache.exist(diskCacheKey))

        runBlocking {
            HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
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
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it, readDelayMillis = 500))
        }

        val testUri = TestHttpStack.testImages.first()
        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, testUri.uriString) {
            downloadCachePolicy(DISABLED)
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }

        val diskCacheKey = request.uriString
        val diskCache = sketch.downloadCache
        diskCache.remove(diskCacheKey)
        Assert.assertFalse(diskCache.exist(diskCacheKey))

        progressList.clear()
        runBlocking {
            val job = launch {
                HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
            }
            delay(2000)
            job.cancel()
        }
        Assert.assertTrue(progressList.size > 0)
        Assert.assertNull(progressList.find { it == testUri.contentLength })
    }

    @Test
    fun testCancel2() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it, readDelayMillis = 500, connectionDelayMillis = 500))
        }

        val testUri = TestHttpStack.testImages.first()
        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, testUri.uriString) {
            downloadCachePolicy(DISABLED)
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }

        val diskCacheKey = request.uriString
        val diskCache = sketch.downloadCache
        diskCache.remove(diskCacheKey)
        Assert.assertFalse(diskCache.exist(diskCacheKey))

        progressList.clear()
        runBlocking {
            val job = launch {
                HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
            }
            delay(500)
            job.cancel()
        }
        Assert.assertTrue(progressList.size == 0)
    }

    @Test
    fun testErrorUrl() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, "http://error.com/sample.jpeg") {
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        runBlocking {
            try {
                HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
                Assert.fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Assert.assertEquals(0, progressList.size)
    }

    @Test
    fun testContentUrl() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val request = ImageRequest(context, TestHttpStack.errorImage.uriString) {
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        runBlocking {
            try {
                HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
                Assert.fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Assert.assertEquals(0, progressList.size)
    }

    @Test
    fun testChunkedImage() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val testUri = TestHttpStack.chunkedErrorImage
        val request = ImageRequest(context, testUri.uriString) {
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()

        runBlocking {
            HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
        }
        Assert.assertTrue(progressList.size == 0)
        Assert.assertNull(progressList.find { it == testUri.contentLength })

        Assert.assertTrue(sketch.downloadCache.exist(request.uriString))
    }

    @Test
    fun testLengthError() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val testUri = TestHttpStack.lengthErrorImage
        val request = ImageRequest(context, testUri.uriString) {
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        runBlocking {
            try {
                HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
                Assert.fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            delay(1000)
        }
        Assert.assertTrue(progressList.size > 0)
        Assert.assertNotNull(progressList.find { it == testUri.contentLength + 1 })

        Assert.assertFalse(sketch.downloadCache.exist(request.uriString))
    }

    @Test
    fun testLengthError2() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val progressList = mutableListOf<Long>()
        val testUri = TestHttpStack.lengthErrorImage
        val request = ImageRequest(context, testUri.uriString) {
            downloadCachePolicy(DISABLED)
            progressListener { _, progress ->
                progressList.add(progress.completedLength)
            }
        }
        sketch.downloadCache.clear()
        runBlocking {
            try {
                HttpUriFetcher.Factory().create(sketch, request)!!.fetch().getOrThrow()
                Assert.fail("No exception thrown")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            delay(1000)
        }
        Assert.assertTrue(progressList.size > 0)
        Assert.assertNotNull(progressList.find { it == testUri.contentLength + 1 })

        Assert.assertFalse(sketch.downloadCache.exist(request.uriString))
    }
}