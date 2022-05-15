package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult

class TestDrawableDecodeInterceptor : DecodeInterceptor<DrawableDecodeResult> {

    override suspend fun intercept(chain: DecodeInterceptor.Chain<DrawableDecodeResult>): DrawableDecodeResult {
        TODO("Not yet implemented")
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