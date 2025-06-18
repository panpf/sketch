package com.github.panpf.sketch.http.okhttp.common.test.http

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.http.OkHttpStack.MyInterceptor
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.measureTime

class OkHttpStackTest {

    @Test
    fun testBuilder() {
        OkHttpStack.Builder().build().apply {
            assertEquals(10_000, okHttpClient.connectTimeoutMillis)
            assertEquals(10_000, okHttpClient.readTimeoutMillis)
            assertNull(okHttpClient.interceptors.find { it is MyInterceptor })
            assertEquals(0, okHttpClient.interceptors.size)
            assertEquals(0, okHttpClient.networkInterceptors.size)
        }

        OkHttpStack.Builder().apply {
            connectTimeoutMillis(2000)
            readTimeoutMillis(3000)
        }.build().apply {
            assertEquals(2000, okHttpClient.connectTimeoutMillis)
            assertEquals(3000, okHttpClient.readTimeoutMillis)
            assertNull(okHttpClient.interceptors.find { it is MyInterceptor })
            assertEquals(0, okHttpClient.interceptors.size)
            assertEquals(0, okHttpClient.networkInterceptors.size)
        }

        OkHttpStack.Builder().apply {
            userAgent("TestUserAgent")
        }.build().apply {
            okHttpClient.interceptors
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    assertEquals("TestUserAgent", userAgent)
                    assertNull(headers)
                    assertNull(addHeaders)
                }
            assertEquals(1, okHttpClient.interceptors.size)
            assertEquals(0, okHttpClient.networkInterceptors.size)
        }

        OkHttpStack.Builder().apply {
            addHeaders("AddHeader1" to "AddHeaderValue1")
            addHeaders(listOf("AddHeader1" to "AddHeaderValue2"))
        }.build().apply {
            okHttpClient.interceptors
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    assertNull(userAgent)
                    assertEquals(
                        listOf(
                            "AddHeader1" to "AddHeaderValue1",
                            "AddHeader1" to "AddHeaderValue2"
                        ),
                        addHeaders
                    )
                    assertNull(headers)
                }
            assertEquals(1, okHttpClient.interceptors.size)
            assertEquals(0, okHttpClient.networkInterceptors.size)
        }

        OkHttpStack.Builder().apply {
            headers("Header1" to "HeaderValue1")
            headers(mapOf("Header1" to "HeaderValue2"))
        }.build().apply {
            okHttpClient.interceptors
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    assertNull(userAgent)
                    assertNull(addHeaders)
                    assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
                }
            assertEquals(1, okHttpClient.interceptors.size)
            assertEquals(0, okHttpClient.networkInterceptors.size)
        }

        OkHttpStack.Builder().apply {
            userAgent("TestUserAgent")
            addHeaders("AddHeader1" to "AddHeaderValue1")
            addHeaders("AddHeader1" to "AddHeaderValue2")
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            okHttpClient.interceptors
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    assertEquals("TestUserAgent", userAgent)
                    assertEquals(
                        listOf(
                            "AddHeader1" to "AddHeaderValue1",
                            "AddHeader1" to "AddHeaderValue2"
                        ),
                        addHeaders
                    )
                    assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
                }
            assertEquals(1, okHttpClient.interceptors.size)
            assertEquals(0, okHttpClient.networkInterceptors.size)
        }

        OkHttpStack.Builder().apply {
            interceptors(
                MyInterceptor(null, null, null),
                MyInterceptor(null, null, null)
            )
            networkInterceptors(MyInterceptor(null, null, null))
        }.build().apply {
            assertEquals(2, okHttpClient.interceptors.size)
            assertEquals(1, okHttpClient.networkInterceptors.size)
        }
    }

    @Test
    fun testGetResponse() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"
        OkHttpStack.Builder().build().getResponse(url, null, null).apply {
            assertEquals(200, code)
            assertEquals("", message)
            assertEquals(9904, contentLength)
            assertEquals("image/png", contentType)
            assertEquals("image/png", getHeaderField("Content-Type"))
            content().use {}
        }

        OkHttpStack.Builder().apply {
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
            assertEquals("", message)
            assertEquals(9904, contentLength)
            assertEquals("image/png", contentType)
            assertEquals("image/png", getHeaderField("Content-Type"))
            content().use {}
        }

        assertFailsWith(IllegalArgumentException::class) {
            OkHttpStack.Builder().build().getResponse("", null, null)
        }
    }

    @Test
    fun testGetResponseTime() = runTest {
        val duration = measureTime {
            OkHttpStack.Builder().build().getResponse(
                url = "https://img.picgo.net/2025/06/15/1de439443053e5edd.webp",
                httpHeaders = null,
                extras = null
            )
        }
        assertTrue(
            actual = duration.inWholeMilliseconds <= 1500,
            message = "Request took too long: $duration"
        )
    }

    @Test
    fun testRequest() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"
        OkHttpStack.Builder().build().request(url, null, null) {
            assertEquals(200, it.code)
            assertEquals("", it.message)
            assertEquals(9904, it.contentLength)
            assertEquals("image/png", it.contentType)
            assertEquals("image/png", it.getHeaderField("Content-Type"))
            it.content().use {}
        }

        OkHttpStack.Builder().apply {
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
            assertEquals("", it.message)
            assertEquals(9904, it.contentLength)
            assertEquals("image/png", it.contentType)
            assertEquals("image/png", it.getHeaderField("Content-Type"))
            it.content().use {}
        }

        assertFailsWith(IllegalArgumentException::class) {
            OkHttpStack.Builder().build().getResponse("", null, null)
        }
    }

    @Test
    fun testRequestTime() = runTest {
        val duration = measureTime {
            OkHttpStack.Builder().build().request(
                url = "https://img.picgo.net/2025/06/15/1de439443053e5edd.webp",
                httpHeaders = null,
                extras = null
            ) {}
        }
        assertTrue(
            actual = duration.inWholeMilliseconds <= 1500,
            message = "Request took too long: $duration"
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val okHttpClient = OkHttpClient.Builder().build()
        val element1 = OkHttpStack(okHttpClient)
        val element11 = OkHttpStack(okHttpClient)
        val element2 = OkHttpStack(OkHttpClient.Builder().build())
        val element3 = OkHttpStack(OkHttpClient.Builder().build())

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "OkHttpStack",
            OkHttpStack.Builder().build().toString()
        )
        assertEquals(
            "OkHttpStack(connectTimeout=7000,readTimeout=5000,interceptors=[MyInterceptor(userAgent=Android 8.1, headers={header1=value1}, addHeaders=[(header2, value2)])])",
            OkHttpStack.Builder().apply {
                connectTimeoutMillis(7000)
                readTimeoutMillis(5000)
                userAgent("Android 8.1")
                headers("header1" to "value1")
                addHeaders("header2" to "value2")
            }.build().toString()
        )
    }
}