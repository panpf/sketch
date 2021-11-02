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
import com.github.panpf.sketch.util.SketchUtils
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*

class HurlStack : HttpStack {

    override var readTimeout = HttpStack.DEFAULT_READ_TIMEOUT
        private set
    override var maxRetryCount = HttpStack.DEFAULT_MAX_RETRY_COUNT
        private set
    override var connectTimeout = HttpStack.DEFAULT_CONNECT_TIMEOUT
        private set
    override var userAgent: String? = null
        private set
    override var extraHeaders: Map<String?, String?>? = null
        private set
    override var addExtraHeaders: Map<String?, String?>? = null
        private set

    override fun setMaxRetryCount(maxRetryCount: Int): HurlStack {
        this.maxRetryCount = maxRetryCount
        return this
    }

    override fun setConnectTimeout(connectTimeout: Int): HurlStack {
        this.connectTimeout = connectTimeout
        return this
    }

    override fun setReadTimeout(readTimeout: Int): HurlStack {
        this.readTimeout = readTimeout
        return this
    }

    override fun setUserAgent(userAgent: String?): HurlStack {
        this.userAgent = userAgent
        return this
    }

    override fun setExtraHeaders(extraHeaders: Map<String?, String?>?): HurlStack {
        this.extraHeaders = extraHeaders
        return this
    }

    override fun addExtraHeaders(extraHeaders: Map<String?, String?>?): HurlStack {
        addExtraHeaders = extraHeaders
        return this
    }

    override fun canRetry(throwable: Throwable): Boolean {
        return throwable is SocketTimeoutException
    }

    override fun toString(): String {
        return String.format(
            Locale.US, "%s(maxRetryCount=%d,connectTimeout=%d,readTimeout=%d,userAgent=%s)",
            KEY, maxRetryCount, connectTimeout, readTimeout, userAgent
        )
    }

    @Throws(IOException::class)
    override fun getResponse(uri: String?): HttpStack.Response {
        val connection = URL(uri).openConnection() as HttpURLConnection
        connection.connectTimeout = connectTimeout
        connection.readTimeout = readTimeout
        connection.doInput = true
        if (userAgent != null) {
            connection.setRequestProperty("User-Agent", userAgent)
        }
        if (addExtraHeaders != null && addExtraHeaders!!.isNotEmpty()) {
            for ((key, value) in addExtraHeaders!!) {
                connection.addRequestProperty(key, value)
            }
        }
        if (extraHeaders != null && extraHeaders!!.isNotEmpty()) {
            for ((key, value) in extraHeaders!!) {
                connection.setRequestProperty(key, value)
            }
        }
        processRequest(uri, connection)
        connection.connect()
        return HurlResponse(connection)
    }

    protected fun processRequest(uri: String?, connection: HttpURLConnection?) {

    }

    private class HurlResponse(private val connection: HttpURLConnection) : HttpStack.Response {

        @get:Throws(IOException::class)
        override val code: Int
            get() = connection.responseCode

        @get:Throws(IOException::class)
        override val message: String?
            get() = connection.responseMessage

        override val contentLength: Long
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connection.contentLengthLong
            } else {
                getHeaderFieldLong("content-length", -1)
            }

        override val contentType: String?
            get() = connection.contentType

        override val headersString: String?
            get() {
                val headers = connection.headerFields
                if (headers == null || headers.isEmpty()) {
                    return null
                }
                val stringBuilder = StringBuilder()
                stringBuilder.append("[")
                for ((key, values) in headers) {
                    if (stringBuilder.length != 1) {
                        stringBuilder.append(", ")
                    }
                    stringBuilder.append("{")
                    stringBuilder.append(key)
                    stringBuilder.append(":")
                    if (values.size == 0) {
                        stringBuilder.append("")
                    } else if (values.size == 1) {
                        stringBuilder.append(values[0])
                    } else {
                        stringBuilder.append(values.toString())
                    }
                    stringBuilder.append("}")
                }
                stringBuilder.append("]")
                return stringBuilder.toString()
            }

        @get:Throws(IOException::class)
        override val content: InputStream
            get() = connection.inputStream

        override fun releaseConnection() {
            try {
                SketchUtils.close(content)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override val isContentChunked: Boolean
            get() {
                var transferEncodingValue = connection.getHeaderField("Transfer-Encoding")
                if (transferEncodingValue != null) {
                    transferEncodingValue = transferEncodingValue.trim { it <= ' ' }
                }
                return "chunked".equals(transferEncodingValue, ignoreCase = true)
            }
        override val contentEncoding: String?
            get() = connection.contentEncoding

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
                val value = connection.getHeaderField(name)
                try {
                    value.toLong()
                } catch (e: Exception) {
                    defaultValue
                }
            }
        }
    }

    companion object {
        private const val KEY = "HurlStack"
    }
}