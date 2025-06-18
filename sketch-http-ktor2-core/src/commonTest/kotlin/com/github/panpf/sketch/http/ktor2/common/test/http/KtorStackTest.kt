package com.github.panpf.sketch.http.ktor2.common.test.http

import com.github.panpf.sketch.http.HttpHeaders.Builder
import com.github.panpf.sketch.http.KtorStack
import kotlinx.coroutines.test.runTest
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlin.time.measureTime

class KtorStackTest {

    @Test
    fun testGetResponse() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"
        KtorStack().getResponse(url, null, null).apply {
            assertEquals(200, code)
            assertEquals("OK", message)
            assertEquals(9904, contentLength)
            assertEquals("image/png", contentType)
            assertEquals("image/png", getHeaderField("Content-Type"))
            content().use {}
        }

        KtorStack().getResponse(
            url = url,
            httpHeaders = Builder().apply {
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

        assertFails {
            KtorStack().getResponse("", null, null)
        }
    }

    @Test
    fun testGetResponseTime() = runTest {
        val duration = measureTime {
            KtorStack().getResponse(
                url = "https://img.picgo.net/2025/06/15/1de439443053e5edd.webp",
                httpHeaders = null,
                extras = null
            )
        }
        assertTrue(
            actual = duration.inWholeMilliseconds >= 2000,
            message = "Request took too long: $duration"
        )
    }

    @Test
    fun testRequest() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"
        KtorStack().request<Unit>(url, null, null) {
            assertEquals(200, it.code)
            assertEquals("OK", it.message)
            assertEquals(9904, it.contentLength)
            assertEquals("image/png", it.contentType)
            assertEquals("image/png", it.getHeaderField("Content-Type"))
            it.content().use {}
        }

        KtorStack().request<Unit>(
            url = url,
            httpHeaders = Builder().apply {
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

        assertFails {
            KtorStack().request("", null, null) {}
        }
    }

    @Test
    fun testRequestTime() = runTest {
        val duration = measureTime {
            KtorStack().request(
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
}