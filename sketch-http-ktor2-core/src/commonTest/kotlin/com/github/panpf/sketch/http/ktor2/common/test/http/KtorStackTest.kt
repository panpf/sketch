package com.github.panpf.sketch.http.ktor2.common.test.http

import com.github.panpf.sketch.http.HttpHeaders.Builder
import com.github.panpf.sketch.http.KtorStack
import kotlinx.coroutines.test.runTest
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class KtorStackTest {

    @Test
    fun testGetResponse() = runTest {
        val url = "https://inews.gtimg.com/newsapp_bt/0/12171811596_909/0"
        KtorStack()
            .getResponse(url, null, null)
            .apply {
                assertEquals(200, code)
                assertEquals("OK", message)
                assertEquals(9904, contentLength)
                assertEquals("image/png", contentType)
                assertEquals("image/png", getHeaderField("Content-Type"))
                content().use {
                    assertNotNull(it)
                }
            }

        val httpHeaders = Builder().apply {
            add("addHttpHeader1", "setHttpValue1")
            set("setHttpHeader1", "setHttpValue1")
        }.build()
        KtorStack()
            .getResponse(url, httpHeaders, null)
            .apply {
                assertEquals(200, code)
                assertEquals("OK", message)
                assertEquals(9904, contentLength)
                assertEquals("image/png", contentType)
                assertEquals("image/png", getHeaderField("Content-Type"))
                content().use {
                    assertNotNull(it)
                }
            }

        assertFails {
            KtorStack().getResponse("", null, null)
        }
    }
}