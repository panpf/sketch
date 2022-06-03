package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult

class TestDrawableDecodeInterceptor : DrawableDecodeInterceptor {

    override suspend fun intercept(chain: DrawableDecodeInterceptor.Chain): DrawableDecodeResult {
        throw UnsupportedOperationException()
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
        return "TestDrawableDecodeInterceptor"
    }
}