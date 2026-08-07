package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.merged
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.Interceptor.Chain
import com.github.panpf.sketch.request.httpHeaders

class PexelsCompatibleInterceptor : Interceptor {

    override val key: String? = null
    override val sortWeight: Int = 0

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        return if (request.uri.toString().contains("://images.pexels.com/")) {
            val newRequest = request.newBuilder().apply {
                val myHttpHeaders = HttpHeaders {
                    set(
                        name = "User-Agent",
                        value = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/120.0.0.0 Safari/537.36"
                    )
                }
                httpHeaders(request.httpHeaders.merged(myHttpHeaders))
            }.build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other != null && this::class == other::class)

    override fun hashCode(): Int = this::class.hashCode()

    override fun toString(): String =
        "PexelsCompatibleInterceptor(sortWeight=$sortWeight)"
}