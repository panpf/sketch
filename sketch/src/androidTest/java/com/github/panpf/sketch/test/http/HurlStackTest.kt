package com.github.panpf.sketch.test.http

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

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
            headers("Header1" to "HeaderValue1")
            headers("Header1" to "HeaderValue2")
        }.build().apply {
            Assert.assertEquals(mapOf("Header1" to "HeaderValue2"), headers)
        }

        HurlStack.Builder().apply {
            onBeforeConnect { _, _ ->

            }
        }.build().apply {
            Assert.assertNotNull(onBeforeConnect)
        }
    }
}