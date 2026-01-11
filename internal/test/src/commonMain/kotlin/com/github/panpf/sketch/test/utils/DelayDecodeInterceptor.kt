package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import kotlinx.coroutines.delay

data class DelayDecodeInterceptor(
    val delay: Long,
    val onIntercept: (() -> Unit)? = null
) : DecodeInterceptor {

    companion object {
        const val SORT_WEIGHT = 0
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        onIntercept?.invoke()
        delay(delay)
        return chain.proceed()
    }
}