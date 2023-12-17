package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult
import kotlinx.coroutines.delay

data class DelayBitmapDecodeInterceptor(val delay: Long) : BitmapDecodeInterceptor {

    override val key: String?
        get() = null

    override val sortWeight: Int
        get() = 0

    override suspend fun intercept(chain: BitmapDecodeInterceptor.Chain): Result<BitmapDecodeResult> {
        delay(delay)
        return chain.proceed()
    }
}