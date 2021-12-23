package com.github.panpf.sketch3.test

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.fetch.FetchResult
import com.github.panpf.sketch3.common.fetch.HttpUriFetcher
import com.github.panpf.sketch3.download.DownloadRequest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HttpUriFetcherTest {

    private val urls =
        arrayOf("http://5b0988e595225.cdn.sohucs.com/images/20171219/fd5717876ab046b8aa889c9aaac4b56c.jpeg")

    @Test
    fun testFactory() {
        val sketch3 = Sketch3.new(InstrumentationRegistry.getContext())
        val httpDownloadRequest = DownloadRequest.new("http://sample.com.sample.jpg")
        val httpsDownloadRequest = DownloadRequest.new("https://sample.com.sample.jpg")
        val ftpDownloadRequest = DownloadRequest.new("ftp://sample.com.sample.jpg")
        val contentDownloadRequest = DownloadRequest.new("content://sample.com.sample.jpg")
        val httpUriFetcherFactory = HttpUriFetcher.Factory()

        Assert.assertNotNull(httpUriFetcherFactory.create(sketch3, httpDownloadRequest))
        Assert.assertNotNull(httpUriFetcherFactory.create(sketch3, httpsDownloadRequest))
        Assert.assertNull(httpUriFetcherFactory.create(sketch3, ftpDownloadRequest))
        Assert.assertNull(httpUriFetcherFactory.create(sketch3, contentDownloadRequest))
    }

    @Test
    fun testFetch() {
        val sketch3 = Sketch3.new(InstrumentationRegistry.getContext())
        val diskCache = sketch3.diskCache
        val httpUriFetcherFactory = HttpUriFetcher.Factory()

        // Loop the test 50 times without making any mistakes
        repeat(50) {
            runBlocking {
                val request = DownloadRequest.new(urls.first())
                val httpUriFetcher = httpUriFetcherFactory.create(sketch3, request)!!
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
}