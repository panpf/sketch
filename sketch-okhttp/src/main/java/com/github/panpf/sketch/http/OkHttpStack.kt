package com.github.panpf.sketch.http

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.DownloadRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import java.io.InputStream
import java.util.concurrent.TimeUnit.MILLISECONDS

class OkHttpStack(private val okHttpClient: OkHttpClient) : HttpStack {

    override fun toString(): String =
        "OkHttpStack(connectTimeout=${okHttpClient.connectTimeoutMillis},readTimeout=${okHttpClient.readTimeoutMillis})"

    override fun getResponse(sketch: Sketch, request: DownloadRequest, url: String): Response {
        val httpRequest = Request.Builder().apply {
            url(url)
            request.httpHeaders?.entries?.forEach {
                header(it.key, it.value)
            }
        }.build()
        return OkHttpResponse(okHttpClient.newCall(httpRequest).execute())
    }

    private class OkHttpResponse(val response: okhttp3.Response) : Response {
        override val code: Int by lazy {
            response.code
        }
        override val message: String? by lazy {
            response.message
        }
        override val contentLength: Long by lazy {
            response.headersContentLength()
        }
        override val contentType: String? by lazy {
            response.header("content-type")
        }
        override val isContentChunked: Boolean by lazy {
            var transferEncodingValue = response.header("Transfer-Encoding")
            if (transferEncodingValue != null) {
                transferEncodingValue = transferEncodingValue.trim { it <= ' ' }
            }
            "chunked".equals(transferEncodingValue, ignoreCase = true)
        }
        override val contentEncoding: String? by lazy {
            response.header("content-encoding")
        }

        override fun getHeaderField(name: String): String? = response.header(name)

        override fun getHeaderFieldInt(name: String, defaultValue: Int): Int =
            response.header(name)?.toIntOrNull() ?: defaultValue

        override fun getHeaderFieldLong(name: String, defaultValue: Long): Long =
            response.header(name)?.toLongOrNull() ?: defaultValue

        override val headersString: String? by lazy {
            val headers = response.headers
            headers.names().joinToString(prefix = "[", postfix = "]") { name ->
                "{${name}:${headers.values(name).joinToString(separator = ",")}}"
            }
        }

        override val content: InputStream
            get() = response.body?.byteStream()!!

        override fun releaseConnection() {
            response.closeQuietly()
        }
    }

    class Builder {
        private var readTimeout: Int = 0
        private var connectTimeout: Int = HttpStack.DEFAULT_CONNECT_TIMEOUT
        private var userAgent: String? = null
        private var extraHeaders: MutableMap<String, String>? = null
        private var addExtraHeaders: MutableMap<String, String>? = null
        private var interceptors: List<Interceptor>? = null
        private var networkInterceptors: List<Interceptor>? = null

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

        fun interceptors(vararg interceptors: Interceptor): Builder = apply {
            this.interceptors = interceptors.toList()
        }

        fun networkInterceptors(vararg networkInterceptors: Interceptor): Builder = apply {
            this.networkInterceptors = networkInterceptors.toList()
        }

        fun build(): OkHttpStack {
            val userAgent = userAgent
            val extraHeaders = extraHeaders?.toMap()
            val addExtraHeaders = addExtraHeaders?.toMap()
            val okHttpClient = OkHttpClient.Builder().apply {
                readTimeout(readTimeout.toLong(), MILLISECONDS)
                connectTimeout(connectTimeout.toLong(), MILLISECONDS)
                addInterceptor { chain ->
                    val newRequest =
                        if (userAgent != null || extraHeaders?.isNotEmpty() == true || addExtraHeaders?.isNotEmpty() == true) {
                            chain.request().newBuilder().apply {
                                if (userAgent != null) {
                                    header("User-Agent", userAgent)
                                }
                                addExtraHeaders?.entries?.forEach {
                                    addHeader(it.key, it.value)
                                }
                                extraHeaders?.entries?.forEach {
                                    header(it.key, it.value)
                                }
                            }.build()
                        } else {
                            chain.request()
                        }
                    chain.proceed(newRequest)
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
}