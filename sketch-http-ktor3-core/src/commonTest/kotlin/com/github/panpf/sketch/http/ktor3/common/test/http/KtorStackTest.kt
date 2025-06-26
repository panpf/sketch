@file:OptIn(ExperimentalTime::class)

package com.github.panpf.sketch.http.ktor3.common.test.http

import com.github.panpf.sketch.http.HttpHeaders.Builder
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.test.utils.readAllBytes
import kotlinx.coroutines.test.runTest
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
        val time1 = Clock.System.now().toEpochMilliseconds()
        val response = KtorStack().getResponse(
            url = "https://img.picgo.net/2025/06/15/1de439443053e5edd.webp",
            httpHeaders = null,
            extras = null
        )
        val time2 = Clock.System.now().toEpochMilliseconds()
        val openTime = time2 - time1
        val bytes = response.readAllBytes()
        val time3 = Clock.System.now().toEpochMilliseconds()
        val totalTime = time3 - time1
        assertEquals(expected = 13365162, actual = bytes.size)
        assertTrue(
            actual = openTime >= totalTime * 0.9,
            message = "openTime=${openTime}ms, totalTime=${totalTime}ms"
        )
    }

    @Test
    fun testRequest() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"
        KtorStack().request(url, null, null) {
            assertEquals(200, it.code)
            assertEquals("OK", it.message)
            assertEquals(9904, it.contentLength)
            assertEquals("image/png", it.contentType)
            assertEquals("image/png", it.getHeaderField("Content-Type"))
            it.content().use {}
        }

        KtorStack().request(
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
        val time1 = Clock.System.now().toEpochMilliseconds()
        KtorStack().request(
            url = "https://img.picgo.net/2025/06/15/1de439443053e5edd.webp",
            httpHeaders = null,
            extras = null
        ) { response ->
            val time2 = Clock.System.now().toEpochMilliseconds()
            val openTime = time2 - time1
            val bytes = response.readAllBytes()
            val time3 = Clock.System.now().toEpochMilliseconds()
            val totalTime = time3 - time1
            assertEquals(expected = 13365162, actual = bytes.size)
            assertTrue(
                actual = openTime <= totalTime / 2,
                message = "openTime=${openTime}ms, totalTime=${totalTime}ms"
            )
        }
    }
}