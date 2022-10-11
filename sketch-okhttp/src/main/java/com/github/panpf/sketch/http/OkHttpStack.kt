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
package com.github.panpf.sketch.http

import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.ImageRequest
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.util.concurrent.TimeUnit.MILLISECONDS

class OkHttpStack(val okHttpClient: OkHttpClient) : HttpStack {

    override suspend fun getResponse(request: ImageRequest, url: String): Response {
        val httpRequest = Request.Builder().apply {
            url(url)
            request.httpHeaders?.apply {
                addList.forEach {
                    addHeader(it.first, it.second)
                }
                setList.forEach {
                    header(it.first, it.second)
                }
            }
        }.build()
        return OkHttpResponse(okHttpClient.newCall(httpRequest).execute())
    }

    override fun toString(): String =
        "OkHttpStack(connectTimeout=${okHttpClient.connectTimeoutMillis()},readTimeout=${okHttpClient.readTimeoutMillis()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OkHttpStack) return false
        if (okHttpClient != other.okHttpClient) return false
        return true
    }

    override fun hashCode(): Int {
        return okHttpClient.hashCode()
    }

    private class OkHttpResponse(val response: okhttp3.Response) : Response {
        override val code: Int by lazy {
            response.code()
        }
        override val message: String? by lazy {
            response.message()
        }
        override val contentLength: Long by lazy {
            response.header("Content-Length")?.toLongOrNull() ?: -1L
        }
        override val contentType: String? by lazy {
            response.header("content-type")
        }

        override fun getHeaderField(name: String): String? = response.header(name)

        override val content: InputStream
            get() = response.body()?.byteStream()!!
    }

    class Builder {
        private var connectTimeoutMillis: Int = HttpStack.DEFAULT_TIMEOUT
        private var readTimeoutMillis: Int = HttpStack.DEFAULT_TIMEOUT
        private var userAgent: String? = null
        private var extraHeaders: MutableMap<String, String>? = null
        private var addExtraHeaders: MutableList<Pair<String, String>>? = null
        private var interceptors: List<Interceptor>? = null
        private var networkInterceptors: List<Interceptor>? = null
        private var enabledTlsProtocols: List<String>? = null

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
            this.extraHeaders = (this.extraHeaders ?: HashMap()).apply {
                putAll(headers)
            }
        }

        /**
         * Set HTTP header
         */
        fun headers(vararg headers: Pair<String, String>): Builder = apply {
            headers(headers.toMap())
        }

        /**
         * Set repeatable HTTP headers
         */
        fun addHeaders(headers: List<Pair<String, String>>): Builder = apply {
            this.addExtraHeaders = (this.addExtraHeaders ?: ArrayList()).apply {
                addAll(headers)
            }
        }

        /**
         * Set repeatable HTTP headers
         */
        fun addHeaders(vararg headers: Pair<String, String>): Builder = apply {
            addHeaders(headers.toList())
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

        /**
         * Set tls protocols
         */
        fun enabledTlsProtocols(vararg enabledTlsProtocols: String): Builder = apply {
            this.enabledTlsProtocols = enabledTlsProtocols.toList()
        }

        /**
         * Set tls protocols
         */
        fun enabledTlsProtocols(enabledTlsProtocols: List<String>): Builder = apply {
            this.enabledTlsProtocols = enabledTlsProtocols.toList()
        }

        fun build(): OkHttpStack {
            val okHttpClient = OkHttpClient.Builder().apply {
                connectTimeout(connectTimeoutMillis.toLong(), MILLISECONDS)
                readTimeout(readTimeoutMillis.toLong(), MILLISECONDS)
                val userAgent = userAgent
                val extraHeaders = extraHeaders?.toMap()?.takeIf { it.isNotEmpty() }
                val addExtraHeaders = addExtraHeaders?.toList()?.takeIf { it.isNotEmpty() }
                if (userAgent != null || extraHeaders != null || addExtraHeaders != null) {
                    addInterceptor(MyInterceptor(userAgent, extraHeaders, addExtraHeaders))
                }
                interceptors?.forEach { interceptor ->
                    addInterceptor(interceptor)
                }
                networkInterceptors?.forEach { interceptor ->
                    addNetworkInterceptor(interceptor)
                }
                val enabledTlsProtocols = enabledTlsProtocols
                if (enabledTlsProtocols?.isNotEmpty() == true) {
                    setEnabledTlsProtocols(enabledTlsProtocols.toTypedArray())
                }
            }.build()
            return OkHttpStack(okHttpClient)
        }
    }

    class MyInterceptor(
        val userAgent: String?,
        val headers: Map<String, String>? = null,
        val addHeaders: List<Pair<String, String>>? = null,
    ) : Interceptor {

        override fun intercept(chain: Chain): okhttp3.Response =
            chain.proceed(setupAttrs(chain.request()))

        private fun setupAttrs(request: Request): Request =
            request.newBuilder().apply {
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
    }
}