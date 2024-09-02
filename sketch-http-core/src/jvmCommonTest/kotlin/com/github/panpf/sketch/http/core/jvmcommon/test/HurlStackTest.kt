package com.github.panpf.sketch.http.core.jvmcommon.test

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack.Builder
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

class HurlStackTest {

    @Test
    fun testBuilder() {
        Builder().build().apply {
            assertEquals(HttpStack.DEFAULT_TIMEOUT, connectTimeoutMillis)
            assertEquals(HttpStack.DEFAULT_TIMEOUT, readTimeoutMillis)
            assertNull(userAgent)
            assertNull(headers)
            assertNull(addHeaders)
            assertNull(onBeforeConnect)
        }

        Builder().apply {
            connectTimeoutMillis(2000)
            readTimeoutMillis(3000)
        }.build().apply {
            assertEquals(2000, connectTimeoutMillis)
            assertEquals(3000, readTimeoutMillis)
        }

        Builder().apply {
            userAgent("TestUserAgent")
        }.build().apply {
            assertEquals("TestUserAgent", userAgent)
        }

        Builder().apply {
            addHeaders("AddHeader1" to "AddHeaderValue1")
            addHeaders("AddHeader1" to "AddHeaderValue2")
        }.build().apply {
            assertEquals(
                listOf(
                    "AddHeader1" to "AddHeaderValue1",
                    "AddHeader1" to "AddHeaderValue2"
                ),
                addHeaders
            )
        }
        Builder().apply {
            addHeaders(listOf())
        }.build().apply {
            assertNull(addHeaders)
        }

        Builder().apply {
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
        }

        Builder().apply {
            headers(mapOf())
        }.build().apply {
            assertNull(headers)
        }

        Builder().apply {
            onBeforeConnect { _, _ ->

            }
        }.build().apply {
            assertNotNull(onBeforeConnect)
        }
    }

    @Test
    fun testGetResponse() {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"

        Builder().build()
            .let { runBlocking { it.getResponse(url, null, null) } }
            .apply {
                assertEquals(200, code)
                assertEquals("OK", message)
                assertEquals(9904, contentLength)
                assertEquals("image/png", contentType)
                assertEquals("image/png", getHeaderField("Content-Type"))
                runBlocking { content() }.use {
                    assertNotNull(it)
                }
            }

        Builder().apply {
            userAgent("Android 8.1")
            headers("header1" to "value1")
            addHeaders("addHeader1" to "addValue1")
        }.build().let {
            val httpHeaders = HttpHeaders.Builder().apply {
                add("addHttpHeader1", "setHttpValue1")
                set("setHttpHeader1", "setHttpValue1")
            }.build()
            runBlocking {
                it.getResponse(url, httpHeaders, null)
            }
        }.apply {
            assertEquals(200, code)
            assertEquals("OK", message)
            assertEquals(9904, contentLength)
            assertEquals("image/png", contentType)
            assertEquals("image/png", getHeaderField("Content-Type"))
            runBlocking { content() }.use {
                assertNotNull(it)
            }
        }

        assertFailsWith(IOException::class) {
            Builder().build().let {
                runBlocking {
                    it.getResponse("", null, null)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = Builder().build()
        val element11 = Builder().build()
        val element2 = Builder().apply {
            readTimeoutMillis(1000)
        }.build()
        val element3 = Builder().apply {
            connectTimeoutMillis(2000)
        }.build()
        val element4 = Builder().apply {
            userAgent("FakeUserAgent")
        }.build()
        val element5 = Builder().apply {
            headers("header1" to "value1")
        }.build()
        val element6 = Builder().apply {
            addHeaders("addHeader1" to "addValue1")
        }.build()
        val element7 = Builder().apply {
            onBeforeConnect { _, _ -> }
        }.build()

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element1, element4)
        assertNotSame(element1, element5)
        assertNotSame(element1, element6)
        assertNotSame(element1, element7)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element1, element6)
        assertNotEquals(element1, element7)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element1.hashCode(), element6.hashCode())
        assertNotEquals(element1.hashCode(), element7.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "HurlStack(connectTimeout=${HttpStack.DEFAULT_TIMEOUT},readTimeout=${HttpStack.DEFAULT_TIMEOUT},userAgent=null)",
            Builder().build().toString()
        )
    }
}