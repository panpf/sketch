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
package com.github.panpf.sketch.test.http

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4a.network.Networkx
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class HurlStackTest {

    @Test
    fun testBuilder() {
        HurlStack.Builder().build().apply {
            Assert.assertEquals(HttpStack.DEFAULT_TIMEOUT, connectTimeoutMillis)
            Assert.assertEquals(HttpStack.DEFAULT_TIMEOUT, readTimeoutMillis)
            Assert.assertNull(userAgent)
            Assert.assertNull(headers)
            Assert.assertNull(addHeaders)
            Assert.assertNull(onBeforeConnect)
        }

        HurlStack.Builder().apply {
            connectTimeoutMillis(2000)
            readTimeoutMillis(3000)
        }.build().apply {
            Assert.assertEquals(2000, connectTimeoutMillis)
            Assert.assertEquals(3000, readTimeoutMillis)
        }

        HurlStack.Builder().apply {
            userAgent("TestUserAgent")
        }.build().apply {
            Assert.assertEquals("TestUserAgent", userAgent)
        }

        HurlStack.Builder().apply {
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
        HurlStack.Builder().apply {
            addHeaders(listOf())
        }.build().apply {
            Assert.assertNull(addHeaders)
        }

        HurlStack.Builder().apply {
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            Assert.assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
        }

        HurlStack.Builder().apply {
            headers(mapOf())
        }.build().apply {
            Assert.assertNull(headers)
        }

        HurlStack.Builder().apply {
            onBeforeConnect { _, _ ->

            }
        }.build().apply {
            Assert.assertNotNull(onBeforeConnect)
        }
    }

    @Test
    fun testGetResponse() {
        val context = getTestContext()
        if (!Networkx.isConnected(context)) return

        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"

        HurlStack.Builder().build().getResponse(DownloadRequest(context, url), url).apply {
            Assert.assertEquals(200, code)
            Assert.assertEquals("OK", message)
            Assert.assertEquals(9904, contentLength)
            Assert.assertEquals("image/png", contentType)
            Assert.assertEquals("image/png", getHeaderField("Content-Type"))
            content.use {
                Assert.assertNotNull(it)
            }
        }

        HurlStack.Builder().apply {
            userAgent("Android 8.1")
            headers("header1" to "value1")
            addHeaders("addHeader1" to "addValue1")
        }.build().getResponse(DownloadRequest(context, url) {
            addHttpHeader("addHttpHeader1", "setHttpValue1")
            setHttpHeader("setHttpHeader1", "setHttpValue1")
        }, url).apply {
            Assert.assertEquals(200, code)
            Assert.assertEquals("OK", message)
            Assert.assertEquals(9904, contentLength)
            Assert.assertEquals("image/png", contentType)
            Assert.assertEquals("image/png", getHeaderField("Content-Type"))
            content.use {
                Assert.assertNotNull(it)
            }
        }

        assertThrow(IOException::class) {
            HurlStack.Builder().build().getResponse(DownloadRequest(context, url), "")
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
        val element7 = HurlStack.Builder().apply {
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
            HurlStack.Builder().build().toString()
        )
    }
}