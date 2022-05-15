package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class TestRequestInterceptor : RequestInterceptor {

    override suspend fun intercept(chain: Chain): ImageData {
        return chain.proceed(chain.request)
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
        return "TestRequestInterceptor"
    }
}