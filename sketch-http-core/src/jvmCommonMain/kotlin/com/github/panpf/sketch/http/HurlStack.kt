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
import com.github.panpf.sketch.http.internal.TlsCompatSocketFactory
import com.github.panpf.sketch.request.Extras
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Use [HttpURLConnection] to request HTTP
 */
class HurlStack private constructor(
    /**
     * Read timeout, in milliseconds
     */
    val readTimeoutMillis: Int = HttpStack.DEFAULT_TIMEOUT,

    /**
     * Connection timeout, in milliseconds
     */
    val connectTimeoutMillis: Int = HttpStack.DEFAULT_TIMEOUT,

    /**
     * HTTP UserAgent
     */
    val userAgent: String? = null,

    /**
     * HTTP header
     */
    val headers: Map<String, String>? = null,

    /**
     * Repeatable HTTP headers
     */
    val addHeaders: List<Pair<String, String>>? = null,

    /**
     * Callback before executing connection
     */
    val onBeforeConnect: ((url: String, connection: HttpURLConnection) -> Unit)? = null,

    /**
     * Enabled tls protocols
     */
    val enabledTlsProtocols: Array<String>? = null
) : HttpStack {

    @Throws(IOException::class)
    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        extras: Extras?
    ): Response {
        var newUri = url
        while (newUri.isNotEmpty()) {
            val connection = (URL(newUri).openConnection() as HttpURLConnection).apply {
                this@apply.connectTimeout = this@HurlStack.connectTimeoutMillis
                this@apply.readTimeout = this@HurlStack.readTimeoutMillis
                doInput = true
                if (this@HurlStack.userAgent != null) {
                    setRequestProperty("User-Agent", this@HurlStack.userAgent)
                }
                if (!addHeaders.isNullOrEmpty()) {
                    for ((key, value) in addHeaders) {
                        addRequestProperty(key, value)
                    }
                }
                if (!headers.isNullOrEmpty()) {
                    for ((key, value) in headers) {
                        setRequestProperty(key, value)
                    }
                }
                httpHeaders?.apply {
                    addList.forEach {
                        addRequestProperty(it.first, it.second)
                    }
                    setList.forEach {
                        setRequestProperty(it.first, it.second)
                    }
                }
                val enabledTlsProtocols = enabledTlsProtocols
                if (this is HttpsURLConnection && enabledTlsProtocols?.isNotEmpty() == true) {
                    sslSocketFactory = TlsCompatSocketFactory(enabledTlsProtocols)
                }
            }
            onBeforeConnect?.invoke(url, connection)
            // Currently running on a limited number of IO contexts, so this warning can be ignored
            connection.connect()
            val code = connection.responseCode
            if (code == 301 || code == 302 || code == 307) {
                newUri = connection.getHeaderField("Location")
            } else {
                return HurlResponse(connection)
            }
        }
        throw throw IOException("Unable to get response")
    }

    override fun toString(): String =
        "HurlStack(connectTimeout=${connectTimeoutMillis},readTimeout=${readTimeoutMillis},userAgent=${userAgent})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HurlStack) return false
        if (readTimeoutMillis != other.readTimeoutMillis) return false
        if (connectTimeoutMillis != other.connectTimeoutMillis) return false
        if (userAgent != other.userAgent) return false
        if (headers != other.headers) return false
        if (addHeaders != other.addHeaders) return false
        if (onBeforeConnect != other.onBeforeConnect) return false
        return true
    }

    override fun hashCode(): Int {
        var result = readTimeoutMillis
        result = 31 * result + connectTimeoutMillis
        result = 31 * result + (userAgent?.hashCode() ?: 0)
        result = 31 * result + (headers?.hashCode() ?: 0)
        result = 31 * result + (addHeaders?.hashCode() ?: 0)
        result = 31 * result + (onBeforeConnect?.hashCode() ?: 0)
        return result
    }

    class HurlResponse(
        @Suppress("MemberVisibilityCanBePrivate") val connection: HttpURLConnection
    ) : Response {

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
        private var extraHeaders: MutableMap<String, String>? = null
        private var addExtraHeaders: MutableList<Pair<String, String>>? = null
        private var onBeforeConnect: ((url: String, connection: HttpURLConnection) -> Unit)? = null
        private var enabledTlsProtocols: List<String>? = null

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
         * Callback before executing connection
         */
        fun onBeforeConnect(block: (url: String, connection: HttpURLConnection) -> Unit): Builder =
            apply {
                this.onBeforeConnect = block
            }

        /**
         * Set tls protocols
         */
        @Suppress("unused")
        fun enabledTlsProtocols(vararg enabledTlsProtocols: String): Builder = apply {
            this.enabledTlsProtocols = enabledTlsProtocols.toList()
        }

        /**
         * Set tls protocols
         */
        @Suppress("unused")
        fun enabledTlsProtocols(enabledTlsProtocols: List<String>): Builder = apply {
            this.enabledTlsProtocols = enabledTlsProtocols.toList()
        }

        fun build(): HurlStack = HurlStack(
            readTimeoutMillis = readTimeoutMillis,
            connectTimeoutMillis = connectTimeoutMillis,
            userAgent = userAgent,
            headers = extraHeaders?.takeIf { it.isNotEmpty() },
            addHeaders = addExtraHeaders?.takeIf { it.isNotEmpty() },
            onBeforeConnect = onBeforeConnect,
            enabledTlsProtocols = enabledTlsProtocols?.toTypedArray(),
        )
    }
}