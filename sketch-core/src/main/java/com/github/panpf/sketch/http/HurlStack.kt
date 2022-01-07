/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

import android.os.Build
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadRequest
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HurlStack(
    val readTimeout: Int,
    val connectTimeout: Int,
    val userAgent: String?,
    val extraHeaders: Map<String, String>?,
    val addExtraHeaders: Map<String, String>?,
    val processRequest: ((url: String, connection: HttpURLConnection) -> Unit)?
) : HttpStack {

    override fun toString(): String =
        "HurlStack(connectTimeout=${connectTimeout},readTimeout=${readTimeout},userAgent=${userAgent})"

    @Throws(IOException::class)
    override fun getResponse(
        sketch: Sketch,
        request: DownloadRequest,
        url: String
    ): HttpStack.Response {
        var newUri = url
        while (newUri.isNotEmpty()) {
            val connection = (URL(newUri).openConnection() as HttpURLConnection).apply {
                connectTimeout = connectTimeout
                readTimeout = readTimeout
                doInput = true
                if (userAgent != null) {
                    setRequestProperty("User-Agent", userAgent)
                }
                if (addExtraHeaders != null && addExtraHeaders.isNotEmpty()) {
                    for ((key, value) in addExtraHeaders) {
                        addRequestProperty(key, value)
                    }
                }
                if (extraHeaders != null && extraHeaders.isNotEmpty()) {
                    for ((key, value) in extraHeaders) {
                        setRequestProperty(key, value)
                    }
                }
                request.httpHeaders?.forEach {
                    setRequestProperty(it.key, it.value)
                }
                processRequest?.invoke(url, this)
            }
            connection.connect()
            val code = connection.responseCode
            if (code == 301 || code == 302 || code == 307) {
                newUri = connection.getHeaderField("Location")
            } else {
                return HurlResponse(connection)
            }
        }
        throw IOException("Unable to get response")
    }

    private class HurlResponse(private val connection: HttpURLConnection) : HttpStack.Response {

        @get:Throws(IOException::class)
        override val code: Int by lazy { connection.responseCode }

        @get:Throws(IOException::class)
        override val message: String? by lazy { connection.responseMessage }

        override val contentLength: Long by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connection.contentLengthLong
            } else {
                getHeaderFieldLong("content-length", -1)
            }
        }

        override val contentType: String? by lazy {
            connection.contentType
        }

        @get:Throws(IOException::class)
        override val content: InputStream
            get() = connection.inputStream

        override val isContentChunked: Boolean by lazy {
            var transferEncodingValue = connection.getHeaderField("Transfer-Encoding")
            if (transferEncodingValue != null) {
                transferEncodingValue = transferEncodingValue.trim { it <= ' ' }
            }
            "chunked".equals(transferEncodingValue, ignoreCase = true)
        }

        override val contentEncoding: String? by lazy {
            connection.contentEncoding
        }

        override val headersString: String? by lazy {
            connection.headerFields
                .toList()
                .joinToString(prefix = "[", postfix = "]") {
                    "{${it.first}:${it.second.joinToString(separator = ",")}}"
                }
        }

        override fun releaseConnection() {
            try {
                content.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun getHeaderField(name: String): String? {
            return connection.getHeaderField(name)
        }

        override fun getHeaderFieldInt(name: String, defaultValue: Int): Int {
            return connection.getHeaderFieldInt(name, defaultValue)
        }

        override fun getHeaderFieldLong(name: String, defaultValue: Long): Long {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connection.getHeaderFieldLong(name, defaultValue)
            } else {
                connection.getHeaderField(name).toLongOrNull() ?: defaultValue
            }
        }
    }

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): HurlStack = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    companion object {

        fun new(
            configBlock: (Builder.() -> Unit)? = null
        ): HurlStack = Builder().apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {
        private var readTimeout: Int
        private var connectTimeout: Int
        private var userAgent: String?
        private var extraHeaders: MutableMap<String, String>?
        private var addExtraHeaders: MutableMap<String, String>?
        private var processRequest: ((url: String, connection: HttpURLConnection) -> Unit)?

        constructor() {
            this.readTimeout = HttpStack.DEFAULT_READ_TIMEOUT
            this.connectTimeout = HttpStack.DEFAULT_CONNECT_TIMEOUT
            this.userAgent = null
            this.extraHeaders = null
            this.addExtraHeaders = null
            this.processRequest = null
        }

        constructor(hurlStack: HurlStack) {
            this.readTimeout = hurlStack.readTimeout
            this.connectTimeout = hurlStack.connectTimeout
            this.userAgent = hurlStack.userAgent
            this.extraHeaders = hurlStack.extraHeaders?.toMutableMap()
            this.addExtraHeaders = hurlStack.addExtraHeaders?.toMutableMap()
            this.processRequest = hurlStack.processRequest
        }

        fun connectTimeout(connectTimeout: Int) = apply {
            this.connectTimeout = connectTimeout
        }

        fun readTimeout(readTimeout: Int): Builder = apply {
            this.readTimeout = readTimeout
        }

        fun userAgent(userAgent: String?): Builder = apply {
            this.userAgent = userAgent
        }

        fun extraHeaders(headers: Map<String, String>): Builder = apply {
            this.extraHeaders = (this.extraHeaders ?: HashMap<String, String>()).apply {
                putAll(headers)
            }
        }

        fun extraHeaders(vararg headers: Pair<String, String>): Builder = apply {
            this.extraHeaders = (this.extraHeaders ?: HashMap<String, String>()).apply {
                putAll(headers.toMap())
            }
        }

        fun addExtraHeaders(headers: Map<String, String>): Builder = apply {
            this.addExtraHeaders = (this.addExtraHeaders ?: HashMap<String, String>()).apply {
                putAll(headers)
            }
        }

        fun addExtraHeaders(vararg headers: Pair<String, String>): Builder = apply {
            this.addExtraHeaders = (this.addExtraHeaders ?: HashMap<String, String>()).apply {
                putAll(headers.toMap())
            }
        }

        fun processRequest(block: (url: String, connection: HttpURLConnection) -> Unit): Builder =
            apply {
                this.processRequest = block
            }

        fun build(): HurlStack = HurlStack(
            readTimeout = readTimeout,
            connectTimeout = connectTimeout,
            userAgent = userAgent,
            extraHeaders = extraHeaders,
            addExtraHeaders = addExtraHeaders,
            processRequest = processRequest,
        )
    }
}