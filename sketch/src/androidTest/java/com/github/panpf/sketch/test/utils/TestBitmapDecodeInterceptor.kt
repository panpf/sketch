package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeInterceptor

class TestBitmapDecodeInterceptor : DecodeInterceptor<BitmapDecodeResult> {

    override suspend fun intercept(chain: DecodeInterceptor.Chain<BitmapDecodeResult>): BitmapDecodeResult {
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
        return "TestBitmapDecodeInterceptor"
    }
}