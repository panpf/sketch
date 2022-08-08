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
package com.github.panpf.sketch.okhttp.test.http

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.http.OkHttpStack.MyInterceptor
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.tools4a.network.Networkx
import com.github.panpf.tools4j.test.ktx.assertThrow
import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OkHttpStackTest {

    @Test
    fun testBuilder() {
        OkHttpStack.Builder().build().apply {
            Assert.assertEquals(HttpStack.DEFAULT_TIMEOUT, okHttpClient.connectTimeoutMillis())
            Assert.assertEquals(HttpStack.DEFAULT_TIMEOUT, okHttpClient.readTimeoutMillis())
            Assert.assertNull(okHttpClient.interceptors().find { it is MyInterceptor })
            Assert.assertEquals(0, okHttpClient.interceptors().size)
            Assert.assertEquals(0, okHttpClient.networkInterceptors().size)
        }

        OkHttpStack.Builder().apply {
            connectTimeoutMillis(2000)
            readTimeoutMillis(3000)
        }.build().apply {
            Assert.assertEquals(2000, okHttpClient.connectTimeoutMillis())
            Assert.assertEquals(3000, okHttpClient.readTimeoutMillis())
            Assert.assertNull(okHttpClient.interceptors().find { it is MyInterceptor })
            Assert.assertEquals(0, okHttpClient.interceptors().size)
            Assert.assertEquals(0, okHttpClient.networkInterceptors().size)
        }

        OkHttpStack.Builder().apply {
            userAgent("TestUserAgent")
        }.build().apply {
            okHttpClient.interceptors()
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    Assert.assertEquals("TestUserAgent", userAgent)
                    Assert.assertNull(headers)
                    Assert.assertNull(addHeaders)
                }
            Assert.assertEquals(1, okHttpClient.interceptors().size)
            Assert.assertEquals(0, okHttpClient.networkInterceptors().size)
        }

        OkHttpStack.Builder().apply {
            addHeaders("AddHeader1" to "AddHeaderValue1")
            addHeaders("AddHeader1" to "AddHeaderValue2")
        }.build().apply {
            okHttpClient.interceptors()
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    Assert.assertNull(userAgent)
                    Assert.assertEquals(
                        listOf(
                            "AddHeader1" to "AddHeaderValue1",
                            "AddHeader1" to "AddHeaderValue2"
                        ),
                        addHeaders
                    )
                    Assert.assertNull(headers)
                }
            Assert.assertEquals(1, okHttpClient.interceptors().size)
            Assert.assertEquals(0, okHttpClient.networkInterceptors().size)
        }

        OkHttpStack.Builder().apply {
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            okHttpClient.interceptors()
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    Assert.assertNull(userAgent)
                    Assert.assertNull(addHeaders)
                    Assert.assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
                }
            Assert.assertEquals(1, okHttpClient.interceptors().size)
            Assert.assertEquals(0, okHttpClient.networkInterceptors().size)
        }

        OkHttpStack.Builder().apply {
            userAgent("TestUserAgent")
            addHeaders("AddHeader1" to "AddHeaderValue1")
            addHeaders("AddHeader1" to "AddHeaderValue2")
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            okHttpClient.interceptors()
                .find { it is MyInterceptor }
                .let { it as MyInterceptor }
                .apply {
                    Assert.assertEquals("TestUserAgent", userAgent)
                    Assert.assertEquals(
                        listOf(
                            "AddHeader1" to "AddHeaderValue1",
                            "AddHeader1" to "AddHeaderValue2"
                        ),
                        addHeaders
                    )
                    Assert.assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
                }
            Assert.assertEquals(1, okHttpClient.interceptors().size)
            Assert.assertEquals(0, okHttpClient.networkInterceptors().size)
        }

        OkHttpStack.Builder().apply {
            interceptors(
                MyInterceptor(null, null, null),
                MyInterceptor(null, null, null)
            )
            networkInterceptors(MyInterceptor(null, null, null))
        }.build().apply {
            Assert.assertEquals(2, okHttpClient.interceptors().size)
            Assert.assertEquals(1, okHttpClient.networkInterceptors().size)
        }
    }

    @Test
    fun testGetResponse() {
        val context = InstrumentationRegistry.getInstrumentation().context
        if (!Networkx.isConnected(context)) return

        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"

        OkHttpStack.Builder().build().getResponse(DownloadRequest(context, url), url).apply {
            Assert.assertEquals(200, code)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertEquals("", message)
            } else {
                Assert.assertEquals("OK", message)
            }
            Assert.assertEquals(9904, contentLength)
            Assert.assertEquals("image/png", contentType)
            Assert.assertEquals("image/png", getHeaderField("Content-Type"))
            content.use {
                Assert.assertNotNull(it)
            }
        }

        OkHttpStack.Builder().apply {
            userAgent("Android 8.1")
            headers("header1" to "value1")
            addHeaders("addHeader1" to "addValue1")
        }.build().getResponse(DownloadRequest(context, url) {
            addHttpHeader("addHttpHeader1", "setHttpValue1")
            setHttpHeader("setHttpHeader1", "setHttpValue1")
        }, url).apply {
            Assert.assertEquals(200, code)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertEquals("", message)
            } else {
                Assert.assertEquals("OK", message)
            }
            Assert.assertEquals(9904, contentLength)
            Assert.assertEquals("image/png", contentType)
            Assert.assertEquals("image/png", getHeaderField("Content-Type"))
            content.use {
                Assert.assertNotNull(it)
            }
        }

        assertThrow(IllegalArgumentException::class) {
            OkHttpStack.Builder().build().getResponse(DownloadRequest(context, url), "")
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val okHttpClient = OkHttpClient.Builder().build()
        val element1 = OkHttpStack(okHttpClient)
        val element11 = OkHttpStack(okHttpClient)
        val element2 = OkHttpStack(OkHttpClient.Builder().build())
        val element3 = OkHttpStack(OkHttpClient.Builder().build())

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "OkHttpStack(connectTimeout=10000,readTimeout=10000)",
            OkHttpStack(OkHttpClient.Builder().build()).toString()
        )
    }
}