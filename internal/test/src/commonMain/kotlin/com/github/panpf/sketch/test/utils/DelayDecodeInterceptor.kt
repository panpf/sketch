package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import kotlinx.coroutines.delay

data class DelayDecodeInterceptor(
    val delay: Long,
    val onIntercept: (() -> Unit)? = null
) : DecodeInterceptor {

    override val key: String? = null

    override val sortWeight: Int
        get() = 0

    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        onIntercept?.invoke()
        delay(delay)
        return chain.proceed()
    }
}