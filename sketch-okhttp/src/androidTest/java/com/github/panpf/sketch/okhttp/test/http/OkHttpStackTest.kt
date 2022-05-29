package com.github.panpf.sketch.okhttp.test.http

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.http.OkHttpStack.MyInterceptor
import okhttp3.Request
import okhttp3.internal.Version.userAgent
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
    fun testMyInterceptor() {
        val interceptor = MyInterceptor(
            "TestUserAgent",
            mapOf("Header1" to "HeaderValue2"),
            listOf("AddHeader1" to "AddHeaderValue1", "AddHeader1" to "AddHeaderValue2")
        )

        val request = Request.Builder().apply {
            url("http://sample.com/sample.jpeg")
        }.build()
        request.apply {
            Assert.assertEquals(null, header("User-Agent"))
            Assert.assertEquals(null, header("Header1"))
            Assert.assertEquals(listOf<String>(), headers("AddHeader1"))
        }

        interceptor.setupAttrs(request).apply {
            Assert.assertEquals("TestUserAgent", header("User-Agent"))
            Assert.assertEquals("HeaderValue2", header("Header1"))
            Assert.assertEquals(
                listOf("AddHeaderValue1", "AddHeaderValue2"),
                headers("AddHeader1")
            )
        }
    }
}