package com.github.panpf.sketch.sample.ui.util

import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class PexelsCompatibleRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 80

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        return if (request.uriString.contains("://images.pexels.com/")) {
            val newRequest = request.newBuilder().apply {
                addHttpHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                )
            }.build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }

    @Suppress("RedundantOverride")
    override fun equals(other: Any?): Boolean {
        // If you add construction parameters to this class, you need to change it here
        return super.equals(other)
    }

    @Suppress("RedundantOverride")
    override fun hashCode(): Int {
        // If you add construction parameters to this class, you need to change it here
        return super.hashCode()
    }

    override fun toString(): String {
        return "PexelsCompatibleRequestInterceptor(sortWeight=$sortWeight)"
    }
}