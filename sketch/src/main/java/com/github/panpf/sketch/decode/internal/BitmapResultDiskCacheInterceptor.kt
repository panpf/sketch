package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest

class BitmapResultDiskCacheInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult =
        tryLockBitmapResultDiskCache(chain.sketch, chain.request) { helper ->
            helper?.read() ?: chain.proceed().apply {
                helper?.write(this@apply)
            }
        }

    override fun toString(): String = "BitmapResultDiskCacheInterceptor"
}