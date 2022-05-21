package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult

class BitmapResultCacheDecodeInterceptor : BitmapDecodeInterceptor {

    @WorkerThread
    override suspend fun intercept(
        chain: BitmapDecodeInterceptor.Chain,
    ): BitmapDecodeResult =
        tryLockResultCache(chain.sketch, chain.request) { helper ->
            helper?.read() ?: chain.proceed().apply {
                helper?.write(this@apply)
            }
        }

    override fun toString(): String = "BitmapResultCacheDecodeInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}