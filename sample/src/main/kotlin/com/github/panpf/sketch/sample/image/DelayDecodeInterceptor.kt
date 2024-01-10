package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import kotlinx.coroutines.delay

data class DelayDecodeInterceptor(val delay: Long) : DecodeInterceptor {

    override val key: String?
        get() = null

    override val sortWeight: Int
        get() = 0

    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        delay(delay)
        return chain.proceed()
    }
}