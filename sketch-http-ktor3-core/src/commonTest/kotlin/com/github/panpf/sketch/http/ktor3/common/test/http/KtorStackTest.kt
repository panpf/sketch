@file:OptIn(ExperimentalTime::class)

package com.github.panpf.sketch.http.ktor3.common.test.http

import com.github.panpf.sketch.http.HttpHeaders.Builder
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.test.utils.readAllBytes
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.writeByteArray
import io.ktor.utils.io.writer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.ExperimentalTime

class KtorStackTest {

    @Test
    fun testGetResponse() = runTest {
        val content = "The weather is sunny and cloudless today, perfect for going out and playing!"
        val contentBytes = content.toByteArray()
        val mock = MockEngine { request ->
            respond(
                content = writer(coroutineContext = Dispatchers.Unconfined) {
                    channel.writeByteArray(contentBytes)
                }.channel,
                headers = headers {
                    set(HttpHeaders.ContentLength, contentBytes.size.toString())
                    set(HttpHeaders.ContentType, "text/plain")
                    request.headers.entries().filter { it.key.startsWith(prefix = "test") }
                        .forEach {
                            appendAll(name = it.key, values = it.value)
                        }
                }
            )
        }
        val ktorStack = KtorStack(client = HttpClient(engine = mock))

        ktorStack.getResponse(
            url = "http://fake.com/fake.txt",
            httpHeaders = null,
            extras = null
        ).apply {
            assertEquals(expected = 200, actual = this.code)
            assertEquals(expected = "OK", actual = this.message)
            assertEquals(expected = contentBytes.size.toLong(), actual = this.contentLength)
            assertEquals(expected = "text/plain", actual = this.contentType)
            assertEquals(expected = content, actual = this.readAllBytes().decodeToString())
            assertEquals(expected = null, actual = this.getHeaderField(name = "testHttpHeader1"))
            assertEquals(expected = null, actual = this.getHeaderField(name = "testHttpHeader2"))
        }

        ktorStack.getResponse(
            url = "http://fake.com/fake.txt",
            httpHeaders = Builder().apply {
                add(name = "testHttpHeader1", value = "value1")
                set(name = "testHttpHeader2", value = "value2")
            }.build(),
            extras = null
        ).apply {
            assertEquals(expected = 200, actual = this.code)
            assertEquals(expected = "OK", actual = this.message)
            assertEquals(expected = contentBytes.size.toLong(), actual = this.contentLength)
            assertEquals(expected = "text/plain", actual = contentType)
            assertEquals(expected = content, actual = this.readAllBytes().decodeToString())
            assertEquals(
                expected = "value1",
                actual = this.getHeaderField(name = "testHttpHeader1")
            )
            assertEquals(
                expected = "value2",
                actual = this.getHeaderField(name = "testHttpHeader2")
            )
        }

        assertFails {
            KtorStack().getResponse(url = "", httpHeaders = null, extras = null)
        }
    }

    @Test
    fun testRequest() = runTest {
        val content = "The weather is sunny and cloudless today, perfect for going out and playing!"
        val contentBytes = content.toByteArray()
        val mock = MockEngine { request ->
            respond(
                content = writer(coroutineContext = Dispatchers.Unconfined) {
                    channel.writeByteArray(contentBytes)
                }.channel,
                headers = headers {
                    set(HttpHeaders.ContentLength, contentBytes.size.toString())
                    set(HttpHeaders.ContentType, "text/plain")
                    request.headers.entries().filter { it.key.startsWith(prefix = "test") }
                        .forEach {
                            appendAll(name = it.key, values = it.value)
                        }
                }
            )
        }
        val ktorStack = KtorStack(client = HttpClient(engine = mock))

        ktorStack.request(
            url = "http://fake.com/fake.txt",
            httpHeaders = null,
            extras = null
        ) {
            assertEquals(expected = 200, actual = it.code)
            assertEquals(expected = "OK", actual = it.message)
            assertEquals(expected = contentBytes.size.toLong(), actual = it.contentLength)
            assertEquals(expected = "text/plain", actual = it.contentType)
            assertEquals(expected = content, actual = it.readAllBytes().decodeToString())
            assertEquals(expected = null, actual = it.getHeaderField(name = "testHttpHeader1"))
            assertEquals(expected = null, actual = it.getHeaderField(name = "testHttpHeader2"))
        }

        ktorStack.request(
            url = "http://fake.com/fake.txt",
            httpHeaders = Builder().apply {
                add(name = "testHttpHeader1", value = "value1")
                set(name = "testHttpHeader2", value = "value2")
            }.build(),
            extras = null
        ) {
            assertEquals(expected = 200, actual = it.code)
            assertEquals(expected = "OK", actual = it.message)
            assertEquals(expected = contentBytes.size.toLong(), actual = it.contentLength)
            assertEquals(expected = "text/plain", actual = it.contentType)
            assertEquals(expected = content, actual = it.readAllBytes().decodeToString())
            assertEquals(expected = "value1", actual = it.getHeaderField(name = "testHttpHeader1"))
            assertEquals(expected = "value2", actual = it.getHeaderField(name = "testHttpHeader2"))
        }

        assertFails {
            KtorStack().request(url = "", httpHeaders = null, extras = null) {}
        }
    }
}