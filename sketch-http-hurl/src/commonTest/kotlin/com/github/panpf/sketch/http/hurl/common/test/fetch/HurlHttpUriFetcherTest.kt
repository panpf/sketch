package com.github.panpf.sketch.http.hurl.common.test.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.fetch.HurlHttpUriFetcher
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HurlHttpUriFetcherTest {

    @Test
    fun testSupportHurlHttpUri() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportHurlHttpUri()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[HurlHttpUriFetcher]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[])",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportHurlHttpUri()
            supportHurlHttpUri()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[HurlHttpUriFetcher,HurlHttpUriFetcher]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[])",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val httpStack = HurlStack.Builder().build()
        HurlHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
    }

    @Test
    fun testEqualsAndHashCode() {
        val (context, sketch) = getTestContextAndSketch()
        val sketch2 = Sketch(context)
        val httpStack = HurlStack.Builder().build()
        val httpStack2 = HurlStack.Builder().apply {
            connectTimeoutMillis(1000)
        }.build()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val request2 = request.newRequest { memoryCachePolicy(DISABLED) }
        val element1 = HurlHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
        val element11 = HurlHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
        val element2 = HurlHttpUriFetcher(sketch2, httpStack, request, "downloadCacheKey")
        val element3 = HurlHttpUriFetcher(sketch, httpStack2, request, "downloadCacheKey")
        val element4 = HurlHttpUriFetcher(sketch, httpStack, request2, "downloadCacheKey")
        val element5 = HurlHttpUriFetcher(sketch, httpStack, request, "downloadCacheKe2")

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
        val httpStack = HurlStack.Builder().build()
        val httpUriFetcher = HurlHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey2")
        assertEquals(
            expected = "HurlHttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request, downloadCacheKey='downloadCacheKey2')",
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

        val factory = HurlHttpUriFetcher.Factory()
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
        val element1 = HurlHttpUriFetcher.Factory()
        val element11 = HurlHttpUriFetcher.Factory()

        assertEquals(element1, element11)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "HurlHttpUriFetcher",
            actual = HurlHttpUriFetcher.Factory().toString()
        )

        assertEquals(
            expected = "HurlHttpUriFetcher(interceptors=[" +
                    "TimeoutInterceptor(connectTimeout=7000, readTimeout=5000), " +
                    "UserAgentInterceptor(userAgent=Android 8.1), " +
                    "HttpHeadersInterceptor(httpHeaders=HttpHeaders(sets=[header1:value1],adds=[header2:value2]))" +
                    "])",
            actual = HurlHttpUriFetcher.Factory(HurlStack.Builder().apply {
                connectTimeoutMillis(7000)
                readTimeoutMillis(5000)
                userAgent("Android 8.1")
                headers("header1" to "value1")
                addHeaders("header2" to "value2")
            }.build()).toString()
        )
    }
}