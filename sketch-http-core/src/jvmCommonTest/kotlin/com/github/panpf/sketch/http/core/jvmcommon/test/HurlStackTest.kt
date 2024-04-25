package com.github.panpf.sketch.http.core.jvmcommon.test

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HttpStack.Companion
import com.github.panpf.sketch.http.HurlStack.Builder
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class HurlStackTest {

    @Test
    fun testBuilder() {
        Builder().build().apply {
            Assert.assertEquals(HttpStack.DEFAULT_TIMEOUT, connectTimeoutMillis)
            Assert.assertEquals(HttpStack.DEFAULT_TIMEOUT, readTimeoutMillis)
            Assert.assertNull(userAgent)
            Assert.assertNull(headers)
            Assert.assertNull(addHeaders)
            Assert.assertNull(onBeforeConnect)
        }

        Builder().apply {
            connectTimeoutMillis(2000)
            readTimeoutMillis(3000)
        }.build().apply {
            Assert.assertEquals(2000, connectTimeoutMillis)
            Assert.assertEquals(3000, readTimeoutMillis)
        }

        Builder().apply {
            userAgent("TestUserAgent")
        }.build().apply {
            Assert.assertEquals("TestUserAgent", userAgent)
        }

        Builder().apply {
            addHeaders("AddHeader1" to "AddHeaderValue1")
            addHeaders("AddHeader1" to "AddHeaderValue2")
        }.build().apply {
            Assert.assertEquals(
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
            Assert.assertNull(addHeaders)
        }

        Builder().apply {
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            Assert.assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
        }

        Builder().apply {
            headers(mapOf())
        }.build().apply {
            Assert.assertNull(headers)
        }

        Builder().apply {
            onBeforeConnect { _, _ ->

            }
        }.build().apply {
            Assert.assertNotNull(onBeforeConnect)
        }
    }

    @Test
    fun testGetResponse() {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"

        Builder().build()
            .let { runBlocking { it.getResponse(url, null, null) } }
            .apply {
                Assert.assertEquals(200, code)
                Assert.assertEquals("OK", message)
                Assert.assertEquals(9904, contentLength)
                Assert.assertEquals("image/png", contentType)
                Assert.assertEquals("image/png", getHeaderField("Content-Type"))
                runBlocking { content() }.use {
                    Assert.assertNotNull(it)
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
            Assert.assertEquals(200, code)
            Assert.assertEquals("OK", message)
            Assert.assertEquals(9904, contentLength)
            Assert.assertEquals("image/png", contentType)
            Assert.assertEquals("image/png", getHeaderField("Content-Type"))
            runBlocking { content() }.use {
                Assert.assertNotNull(it)
            }
        }

        assertThrow(IOException::class) {
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

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element1, element5)
        Assert.assertNotSame(element1, element6)
        Assert.assertNotSame(element1, element7)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element1, element5)
        Assert.assertNotEquals(element1, element6)
        Assert.assertNotEquals(element1, element7)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element6.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element7.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "HurlStack(connectTimeout=${HttpStack.DEFAULT_TIMEOUT},readTimeout=${HttpStack.DEFAULT_TIMEOUT},userAgent=null)",
            Builder().build().toString()
        )
    }
}