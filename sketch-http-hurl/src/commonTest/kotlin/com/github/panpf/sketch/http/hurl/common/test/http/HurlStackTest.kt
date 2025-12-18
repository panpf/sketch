@file:OptIn(ExperimentalTime::class)

package com.github.panpf.sketch.http.hurl.common.test.http

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.readAllBytes
import kotlinx.coroutines.test.runTest
import okio.use
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HurlStackTest {

    @Test
    fun testBuilder() {
        HurlStack.Builder().build().apply {
            val timeoutInterceptor = interceptors.find { it is HurlStack.TimeoutInterceptor }
                ?.asOrThrow<HurlStack.TimeoutInterceptor>()
            assertEquals(null, timeoutInterceptor?.connectTimeout)
            assertEquals(null, timeoutInterceptor?.readTimeout)

            val userAgentInterceptor = interceptors.find { it is HurlStack.UserAgentInterceptor }
                ?.asOrThrow<HurlStack.UserAgentInterceptor>()
            assertNull(userAgentInterceptor)

            val httpHeadersInterceptor =
                interceptors.find { it is HurlStack.HttpHeadersInterceptor }
                    ?.asOrThrow<HurlStack.HttpHeadersInterceptor>()
            assertNull(httpHeadersInterceptor)

            val testInterceptor = interceptors.find { it is TestInterceptor }
                ?.asOrThrow<TestInterceptor>()
            assertNull(testInterceptor)
        }

        HurlStack.Builder().apply {
            connectTimeoutMillis(2000)
            readTimeoutMillis(3000)
        }.build().apply {
            val timeoutInterceptor = interceptors.find { it is HurlStack.TimeoutInterceptor }
                ?.asOrThrow<HurlStack.TimeoutInterceptor>()
            assertEquals(2000, timeoutInterceptor?.connectTimeout)
            assertEquals(3000, timeoutInterceptor?.readTimeout)
        }

        HurlStack.Builder().apply {
            userAgent("TestUserAgent")
        }.build().apply {
            val userAgentInterceptor = interceptors.find { it is HurlStack.UserAgentInterceptor }
                ?.asOrThrow<HurlStack.UserAgentInterceptor>()
            assertEquals("TestUserAgent", userAgentInterceptor?.userAgent)
        }

        HurlStack.Builder().apply {
            addHeaders(listOf("AddHeader1" to "AddHeaderValue1"))
            addHeaders("AddHeader1" to "AddHeaderValue2")
            headers(mapOf("SetHeader1" to "HeaderValue1"))
            headers("SetHeader1" to "HeaderValue2")
        }.build().apply {
            val httpHeadersInterceptor =
                interceptors.find { it is HurlStack.HttpHeadersInterceptor }
                    ?.asOrThrow<HurlStack.HttpHeadersInterceptor>()
            assertEquals(
                listOf(
                    "AddHeader1" to "AddHeaderValue1",
                    "AddHeader1" to "AddHeaderValue2"
                ),
                httpHeadersInterceptor?.httpHeaders?.addList
            )
            assertEquals(
                listOf("SetHeader1" to "HeaderValue2"),
                httpHeadersInterceptor?.httpHeaders?.setList
            )
        }

        HurlStack.Builder().apply {
            addInterceptor(TestInterceptor())
        }.build().apply {
            val testInterceptor = interceptors.find { it is TestInterceptor }
                ?.asOrThrow<TestInterceptor>()
            assertNotNull(testInterceptor)
        }
    }

    @Test
    fun testGetResponse() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"

        HurlStack.Builder().build().getResponse(url, null, null).apply {
            assertEquals(200, code)
            assertEquals("OK", message)
            assertEquals(9904, contentLength)
            assertEquals("image/png", contentType)
            assertEquals("image/png", getHeaderField("Content-Type"))
            content().use {}
        }

        HurlStack.Builder().apply {
            userAgent("Android 8.1")
            headers("header1" to "value1")
            addHeaders("addHeader1" to "addValue1")
        }.build().getResponse(
            url = url,
            httpHeaders = HttpHeaders.Builder().apply {
                add("addHttpHeader1", "setHttpValue1")
                set("setHttpHeader1", "setHttpValue1")
            }.build(),
            extras = null
        ).apply {
            assertEquals(200, code)
            assertEquals("OK", message)
            assertEquals(9904, contentLength)
            assertEquals("image/png", contentType)
            assertEquals("image/png", getHeaderField("Content-Type"))
            content().use {}
        }

        assertFailsWith(IOException::class) {
            HurlStack.Builder().build().getResponse("", null, null)
        }
    }

    @Test
    fun testGetResponseTime() = runTest {
        val time1 = Clock.System.now().toEpochMilliseconds()
        val response = HurlStack.Builder().build().getResponse(
            url = "https://panpf.github.io/sketch/app/files/sample_long_qmsht.jpg",
            httpHeaders = null,
            extras = null
        )
        val time2 = Clock.System.now().toEpochMilliseconds()
        val openTime = time2 - time1
        val bytes = response.readAllBytes()
        val time3 = Clock.System.now().toEpochMilliseconds()
        val totalTime = time3 - time1
        assertEquals(expected = 8063397, actual = bytes.size)
        assertTrue(
            actual = openTime <= totalTime * 0.8,
            message = "openTime=${openTime}ms, totalTime=${totalTime}ms"
        )
    }

    @Test
    fun testRequest() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"

        HurlStack.Builder().build().request(url, null, null) {
            assertEquals(200, it.code)
            assertEquals("OK", it.message)
            assertEquals(9904, it.contentLength)
            assertEquals("image/png", it.contentType)
            assertEquals("image/png", it.getHeaderField("Content-Type"))
            it.content().use {}
        }

        HurlStack.Builder().apply {
            userAgent("Android 8.1")
            headers("header1" to "value1")
            addHeaders("addHeader1" to "addValue1")
        }.build().request(
            url = url,
            httpHeaders = HttpHeaders.Builder().apply {
                add("addHttpHeader1", "setHttpValue1")
                set("setHttpHeader1", "setHttpValue1")
            }.build(),
            extras = null
        ) {
            assertEquals(200, it.code)
            assertEquals("OK", it.message)
            assertEquals(9904, it.contentLength)
            assertEquals("image/png", it.contentType)
            assertEquals("image/png", it.getHeaderField("Content-Type"))
            it.content().use {}
        }

        assertFailsWith(IOException::class) {
            HurlStack.Builder().build().request("", null, null) {}
        }
    }

    @Test
    fun testRequestTime() = runTest {
        val time1 = Clock.System.now().toEpochMilliseconds()
        HurlStack.Builder().build().request(
            url = "https://panpf.github.io/sketch/app/files/sample_long_qmsht.jpg",
            httpHeaders = null,
            extras = null
        ) { response ->
            val time2 = Clock.System.now().toEpochMilliseconds()
            val openTime = time2 - time1
            val bytes = response.readAllBytes()
            val time3 = Clock.System.now().toEpochMilliseconds()
            val totalTime = time3 - time1
            assertEquals(expected = 8063397, actual = bytes.size)
            assertTrue(
                actual = openTime <= totalTime * 0.5,
                message = "openTime=${openTime}ms, totalTime=${totalTime}ms"
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = HurlStack.Builder().build()
        val element11 = HurlStack.Builder().build()
        val element2 = HurlStack.Builder().apply {
            readTimeoutMillis(1000)
        }.build()
        val element3 = HurlStack.Builder().apply {
            connectTimeoutMillis(2000)
        }.build()
        val element4 = HurlStack.Builder().apply {
            userAgent("FakeUserAgent")
        }.build()
        val element5 = HurlStack.Builder().apply {
            headers("header1" to "value1")
        }.build()
        val element6 = HurlStack.Builder().apply {
            addHeaders("addHeader1" to "addValue1")
        }.build()

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element1, element6)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element1.hashCode(), element6.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "HurlStack",
            actual = HurlStack.Builder().build().toString()
        )
        assertEquals(
            expected = "HurlStack(interceptors=[" +
                    "TimeoutInterceptor(connectTimeout=7000, readTimeout=5000), " +
                    "UserAgentInterceptor(userAgent=Android 8.1), " +
                    "HttpHeadersInterceptor(httpHeaders=HttpHeaders(sets=[header1:value1],adds=[header2:value2]))" +
                    "])",
            actual = HurlStack.Builder().apply {
                connectTimeoutMillis(7000)
                readTimeoutMillis(5000)
                userAgent("Android 8.1")
                headers("header1" to "value1")
                addHeaders("header2" to "value2")
            }.build().toString()
        )
    }

    private class TestInterceptor : HurlStack.Interceptor {
        override fun intercept(chain: HurlStack.Interceptor.Chain): HurlStack.Response {
            return chain.proceed()
        }
    }
}