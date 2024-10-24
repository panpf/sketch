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
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * OkHttp implementation of [HttpStack]
 *
 * @see com.github.panpf.sketch.http.okhttp.common.test.http.OkHttpStackTest
 */
class OkHttpStack(val okHttpClient: OkHttpClient) : HttpStack {

    @Throws(IOException::class)
    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): HttpStack.Response {
        val httpRequest = Request.Builder().apply {
            url(url)
            httpHeaders?.apply {
                addList.forEach {
                    addHeader(it.first, it.second)
                }
                setList.forEach {
                    header(it.first, it.second)
                }
            }
        }.build()
        return Response(okHttpClient.newCall(httpRequest).execute())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OkHttpStack
        if (okHttpClient != other.okHttpClient) return false
        return true
    }

    override fun hashCode(): Int {
        return okHttpClient.hashCode()
    }

    override fun toString(): String = buildString {
        append("OkHttpStack(")
        val beginLength = length

        okHttpClient.connectTimeoutMillis.takeIf { it != 10_000 }?.let {
            if (length > beginLength) append(",")
            append("connectTimeout=$it")
        }

        okHttpClient.readTimeoutMillis.takeIf { it != 10_000 }?.let {
            if (length > beginLength) append(",")
            append("readTimeout=$it")
        }

        okHttpClient.interceptors.takeIf { it.isNotEmpty() }?.let {
            if (length > beginLength) append(",")
            append("interceptors=$it")
        }

        okHttpClient.networkInterceptors.takeIf { it.isNotEmpty() }?.let {
            if (length > beginLength) append(",")
            append("interceptors=$it")
        }

        append(")")
    }.let {
        if (it.endsWith("()")) {
            it.replace("()", "")
        } else {
            it
        }
    }

    class Response(
        @Suppress("MemberVisibilityCanBePrivate") val response: okhttp3.Response
    ) : HttpStack.Response {

        override val code: Int = response.code

        override val message: String = response.message

        override val contentLength: Long = getHeaderField("content-length")?.toLongOrNull() ?: -1L

        override val contentType: String? = getHeaderField("content-type")

        override fun getHeaderField(name: String): String? = response.header(name)

        override suspend fun content(): HttpStack.Content {
            val body = response.body ?: throw IOException("response body is null")
            return Content(body.byteStream())
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
        private var connectTimeoutMillis: Int? = null
        private var readTimeoutMillis: Int? = null
        private var userAgent: String? = null
        private var headers: MutableMap<String, String>? = null
        private var addHeaders: MutableList<Pair<String, String>>? = null
        private var interceptors: List<Interceptor>? = null
        private var networkInterceptors: List<Interceptor>? = null

        /**
         * Set connection timeout, in milliseconds
         */
        fun connectTimeoutMillis(connectTimeout: Int) = apply {
            this.connectTimeoutMillis = connectTimeout
        }

        /**
         * Set read timeout, in milliseconds
         */
        fun readTimeoutMillis(readTimeout: Int): Builder = apply {
            this.readTimeoutMillis = readTimeout
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
            this.headers = (this.headers ?: HashMap()).apply {
                putAll(headers)
            }
        }

        /**
         * Set HTTP header
         */
        fun headers(vararg headers: Pair<String, String>): Builder = apply {
            this.headers = (this.headers ?: HashMap()).apply {
                putAll(headers)
            }
        }

        /**
         * Set repeatable HTTP headers
         */
        fun addHeaders(headers: List<Pair<String, String>>): Builder = apply {
            this.addHeaders = (this.addHeaders ?: ArrayList()).apply {
                addAll(headers)
            }
        }

        /**
         * Set repeatable HTTP headers
         */
        fun addHeaders(vararg headers: Pair<String, String>): Builder = apply {
            this.addHeaders = (this.addHeaders ?: ArrayList()).apply {
                addAll(headers)
            }
        }

        /**
         * Set interceptor
         */
        fun interceptors(vararg interceptors: Interceptor): Builder = apply {
            this.interceptors = interceptors.toList()
        }

        /**
         * Set network interceptor
         */
        fun networkInterceptors(vararg networkInterceptors: Interceptor): Builder = apply {
            this.networkInterceptors = networkInterceptors.toList()
        }

        fun build(): OkHttpStack {
            val okHttpClient = OkHttpClient.Builder().apply {
                val connectTimeoutMillis = this@Builder.connectTimeoutMillis
                if (connectTimeoutMillis != null) {
                    connectTimeout(connectTimeoutMillis.toLong(), MILLISECONDS)
                }
                val readTimeoutMillis = this@Builder.readTimeoutMillis
                if (readTimeoutMillis != null) {
                    readTimeout(readTimeoutMillis.toLong(), MILLISECONDS)
                }
                val userAgent = userAgent
                val extraHeaders = headers?.toMap()?.takeIf { it.isNotEmpty() }
                val addExtraHeaders = addHeaders?.toList()?.takeIf { it.isNotEmpty() }
                if (userAgent != null || extraHeaders != null || addExtraHeaders != null) {
                    addInterceptor(MyInterceptor(userAgent, extraHeaders, addExtraHeaders))
                }
                interceptors?.forEach { interceptor ->
                    addInterceptor(interceptor)
                }
                networkInterceptors?.forEach { interceptor ->
                    addNetworkInterceptor(interceptor)
                }
            }.build()
            return OkHttpStack(okHttpClient)
        }
    }

    data class MyInterceptor(
        val userAgent: String?,
        val headers: Map<String, String>? = null,
        val addHeaders: List<Pair<String, String>>? = null,
    ) : Interceptor {

        override fun intercept(chain: Chain): okhttp3.Response {
            val request = chain.request()
            val newRequest = request.newBuilder().apply {
                if (userAgent?.isNotEmpty() == true) {
                    header("User-Agent", userAgent)
                }
                addHeaders?.forEach {
                    addHeader(it.first, it.second)
                }
                headers?.entries?.forEach {
                    header(it.key, it.value)
                }
            }.build()
            return chain.proceed(newRequest)
        }
    }
}