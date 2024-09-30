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
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Use [HttpURLConnection] to request HTTP
 *
 * @see com.github.panpf.sketch.http.core.jvmcommon.test.HurlStackTest
 */
class HurlStack private constructor(
    val interceptors: List<Interceptor> = emptyList()
) : HttpStack {

    @Throws(IOException::class)
    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): HttpStack.Response {
        var newUri = url
        while (newUri.isNotEmpty()) {
            // Currently running on a limited number of IO contexts, so this warning can be ignored
            @Suppress("BlockingMethodInNonBlockingContext")
            val connection = (URL(newUri).openConnection() as HttpURLConnection).apply {
                doInput = true
            }

            val fixedInterceptors = listOf(HttpHeadersInterceptor(httpHeaders), EngineInterceptor())
            val finalInterceptors = interceptors.plus(fixedInterceptors)
            val response = InterceptorChain(connection, finalInterceptors).proceed()

            val code = response.code
            if (code == 301 || code == 302 || code == 307) {
                val location = response.getHeaderField("Location")
                if (location != null) {
                    newUri = location
                } else {
                    throw throw IOException("Unable to get Location field")
                }
            } else {
                return response
            }
        }
        throw throw IOException("Unable to get response")
    }

    override fun toString(): String = "HurlStack(interceptors=$interceptors)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HurlStack
        if (interceptors != other.interceptors) return false
        return true
    }

    override fun hashCode(): Int {
        return interceptors.hashCode()
    }

    class Response(
        @Suppress("MemberVisibilityCanBePrivate") val connection: HttpURLConnection
    ) : HttpStack.Response {

        @get:Throws(IOException::class)
        override val code: Int = connection.responseCode

        @get:Throws(IOException::class)
        override val message: String? = connection.responseMessage

        override val contentLength: Long = getHeaderField("content-length")?.toLongOrNull() ?: -1L

        override val contentType: String? = getHeaderField("content-type")

        override fun getHeaderField(name: String): String? {
            return connection.getHeaderField(name)
        }

        override suspend fun content(): HttpStack.Content {
            return Content(connection.inputStream)
        }
    }

    class Content(private val inputStream: InputStream) : HttpStack.Content {

        override suspend fun read(buffer: ByteArray): Int {
            // Currently running on a limited number of IO contexts, so this warning can be ignored
            @Suppress("BlockingMethodInNonBlockingContext")
            return inputStream.read(buffer)
        }

        override fun close() {
            inputStream.close()
        }
    }

    class Builder {
        private var connectTimeoutMillis: Int = HttpStack.DEFAULT_TIMEOUT
        private var readTimeoutMillis: Int = HttpStack.DEFAULT_TIMEOUT
        private var userAgent: String? = null
        private var httpHeadersBuilder: HttpHeaders.Builder? = null
        private var interceptors: MutableList<Interceptor>? = null

        /**
         * Set connection timeout, in milliseconds
         */
        fun connectTimeoutMillis(connectTimeoutMillis: Int) = apply {
            this.connectTimeoutMillis = connectTimeoutMillis
        }

        /**
         * Set read timeout, in milliseconds
         */
        fun readTimeoutMillis(readTimeoutMillis: Int): Builder = apply {
            this.readTimeoutMillis = readTimeoutMillis
        }

        /**
         * Set HTTP UserAgent
         */
        fun userAgent(userAgent: String?): Builder = apply {
            this.userAgent = userAgent
        }

        /**
         * Set HTTP header
         */
        fun headers(headers: Map<String, String>): Builder = apply {
            this.httpHeadersBuilder = (httpHeadersBuilder ?: HttpHeaders.Builder()).apply {
                headers.forEach { (t, u) ->
                    set(t, u)
                }
            }
        }

        /**
         * Set HTTP header
         */
        fun headers(vararg headers: Pair<String, String>): Builder = apply {
            this.httpHeadersBuilder = (httpHeadersBuilder ?: HttpHeaders.Builder()).apply {
                headers.forEach { (t, u) ->
                    set(t, u)
                }
            }
        }

        /**
         * Set repeatable HTTP headers
         */
        fun addHeaders(headers: List<Pair<String, String>>): Builder = apply {
            this.httpHeadersBuilder = (httpHeadersBuilder ?: HttpHeaders.Builder()).apply {
                headers.forEach { (t, u) ->
                    add(t, u)
                }
            }
        }

        /**
         * Set repeatable HTTP headers
         */
        fun addHeaders(vararg headers: Pair<String, String>): Builder = apply {
            this.httpHeadersBuilder = (httpHeadersBuilder ?: HttpHeaders.Builder()).apply {
                headers.forEach { (t, u) ->
                    add(t, u)
                }
            }
        }

        /**
         * Add interceptor
         */
        fun addInterceptor(interceptor: Interceptor): Builder = apply {
            this.interceptors = (interceptors ?: mutableListOf()).apply {
                add(interceptor)
            }
        }

        fun build(): HurlStack {
            val finalInterceptors = buildList {
                add(TimeoutInterceptor(connectTimeoutMillis, readTimeoutMillis))
                val userAgent = this@Builder.userAgent
                if (userAgent != null) {
                    add(UserAgentInterceptor(userAgent))
                }
                val httpHeadersBuilder = this@Builder.httpHeadersBuilder
                if (httpHeadersBuilder != null) {
                    add(HttpHeadersInterceptor(httpHeadersBuilder.build()))
                }
                val interceptors = this@Builder.interceptors
                if (interceptors != null) {
                    addAll(interceptors)
                }
            }
            return HurlStack(finalInterceptors)
        }
    }

    interface Interceptor {
        fun intercept(chain: Chain): Response

        interface Chain {
            val connection: HttpURLConnection
            fun proceed(): Response
        }
    }

    private class InterceptorChain(
        override val connection: HttpURLConnection,
        private val interceptors: List<Interceptor>,
        private val index: Int = 0
    ) : Interceptor.Chain {

        override fun proceed(): Response {
            val interceptor = interceptors[index]
            val next = InterceptorChain(connection, interceptors, index + 1)
            return interceptor.intercept(next)
        }
    }

    data class HttpHeadersInterceptor(
        val httpHeaders: HttpHeaders?,
    ) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val connection = chain.connection
            httpHeaders?.addList?.forEach { (key, value) ->
                connection.addRequestProperty(key, value)
            }
            httpHeaders?.setList?.forEach { (key, value) ->
                connection.setRequestProperty(key, value)
            }
            return chain.proceed()
        }
    }

    data class TimeoutInterceptor(
        val connectTimeoutMillis: Int? = null,
        val readTimeoutMillis: Int? = null,
    ) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val connection = chain.connection
            if (connectTimeoutMillis != null) {
                connection.connectTimeout = connectTimeoutMillis
            }
            if (readTimeoutMillis != null) {
                connection.readTimeout = readTimeoutMillis
            }
            return chain.proceed()
        }
    }

    data class UserAgentInterceptor(
        val userAgent: String,
    ) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            chain.connection.setRequestProperty("User-Agent", userAgent)
            return chain.proceed()
        }
    }

    private class EngineInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val connection = chain.connection
            connection.connect()
            return Response(connection)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

        override fun toString(): String {
            return "EngineInterceptor()"
        }
    }
}