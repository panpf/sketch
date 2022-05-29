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

    override fun getResponse(request: ImageRequest, url: String): Response {
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
        if (javaClass != other?.javaClass) return false

        other as OkHttpStack

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
        private var readTimeout: Int = HttpStack.DEFAULT_TIMEOUT
        private var connectTimeout: Int = HttpStack.DEFAULT_TIMEOUT
        private var userAgent: String? = null
        private var extraHeaders: MutableMap<String, String>? = null
        private var addExtraHeaders: MutableList<Pair<String, String>>? = null
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
            this.extraHeaders = (this.extraHeaders ?: HashMap()).apply {
                putAll(headers)
            }
        }

        fun extraHeaders(vararg headers: Pair<String, String>): Builder = apply {
            extraHeaders(headers.toMap())
        }

        fun addExtraHeaders(headers: List<Pair<String, String>>): Builder = apply {
            this.addExtraHeaders = (this.addExtraHeaders ?: ArrayList()).apply {
                addAll(headers)
            }
        }

        fun addExtraHeaders(vararg headers: Pair<String, String>): Builder = apply {
            addExtraHeaders(headers.toList())
        }

        fun interceptors(vararg interceptors: Interceptor): Builder = apply {
            this.interceptors = interceptors.toList()
        }

        fun networkInterceptors(vararg networkInterceptors: Interceptor): Builder = apply {
            this.networkInterceptors = networkInterceptors.toList()
        }

        fun build(): OkHttpStack {
            val okHttpClient = OkHttpClient.Builder().apply {
                readTimeout(readTimeout.toLong(), MILLISECONDS)
                connectTimeout(connectTimeout.toLong(), MILLISECONDS)
                val userAgent = userAgent
                val extraHeaders = extraHeaders?.toMap()?.takeIf { it.isNotEmpty() }
                val addExtraHeaders = addExtraHeaders?.toList()?.takeIf { it.isNotEmpty() }
                if (userAgent != null || extraHeaders?.isNotEmpty() == true || addExtraHeaders?.isNotEmpty() == true) {
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

    class MyInterceptor(
        val userAgent: String?,
        val extraHeaders: Map<String, String>? = null,
        val addExtraHeaders: List<Pair<String, String>>? = null,
    ) : Interceptor {

        override fun intercept(chain: Chain): okhttp3.Response =
            chain.proceed(setupAttrs(chain.request()))

        fun setupAttrs(request: Request): Request =
            request.newBuilder().apply {
                if (userAgent?.isNotEmpty() == true) {
                    header("User-Agent", userAgent)
                }
                addExtraHeaders?.forEach {
                    addHeader(it.first, it.second)
                }
                extraHeaders?.entries?.forEach {
                    header(it.key, it.value)
                }
            }.build()
    }
}