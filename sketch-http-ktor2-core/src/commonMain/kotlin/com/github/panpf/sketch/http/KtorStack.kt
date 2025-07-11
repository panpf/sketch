/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.http

import com.github.panpf.sketch.request.Extras
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel

/**
 * Ktor implementation of HttpStack
 *
 * @see com.github.panpf.sketch.http.ktor2.common.test.http.KtorStackTest
 */
class KtorStack(val client: HttpClient = HttpClient()) : HttpStack {

    override suspend fun <T> request(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?,
        block: suspend (HttpStack.Response) -> T
    ): T {
        val httpRequest = HttpRequestBuilder().apply {
            url(url)
            httpHeaders?.apply {
                addList.forEach {
                    header(it.first, it.second)
                }
                setList.forEach {
                    header(it.first, it.second)
                }
            }
        }
//        val httpResponse: HttpResponse = client.request(httpRequest)  // This way, it will directly read all content into memory, so it cannot be used
        return client.prepareRequest(httpRequest).execute {
            block(Response(it))
        }
    }

    @Deprecated(
        message = "The Ktor version of getResponse() will read all the contents into memory before returning the response. Please use request instead.",
        replaceWith = ReplaceWith("request(url, httpHeaders, extras) { it }")
    )
    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): HttpStack.Response {
        val httpRequest = HttpRequestBuilder().apply {
            url(url)
            httpHeaders?.apply {
                addList.forEach {
                    header(it.first, it.second)
                }
                setList.forEach {
                    header(it.first, it.second)
                }
            }
        }
        val httpResponse = client.request(httpRequest)
        return Response(httpResponse)
    }

    override fun toString(): String = "KtorStack"

    class Response(
        @Suppress("MemberVisibilityCanBePrivate") val httpResponse: HttpResponse
    ) : HttpStack.Response {

        override val code: Int = httpResponse.status.value

        override val message: String = httpResponse.status.description

        override val contentLength: Long =
            getHeaderField(io.ktor.http.HttpHeaders.ContentLength)?.toLongOrNull() ?: -1L

        override val contentType: String? = getHeaderField(io.ktor.http.HttpHeaders.ContentType)

        override fun getHeaderField(name: String): String? {
            return httpResponse.headers[name]
        }

        override suspend fun content(): HttpStack.Content {
            return Content(httpResponse.bodyAsChannel())
        }
    }

    class Content(private val channel: ByteReadChannel) : HttpStack.Content {

        override suspend fun read(buffer: ByteArray): Int {
            return channel.readAvailable(buffer, 0, buffer.size)
        }

        override fun close() {
            channel.cancel()
        }
    }
}