package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import kotlinx.coroutines.delay

data class DelayRequestInterceptor(
    val delay: Long,
    val onIntercept: (() -> Unit)? = null
) : RequestInterceptor {

    companion object {
        const val SORT_WEIGHT = 0
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        onIntercept?.invoke()
        val result = chain.proceed(chain.request)
        delay(delay)
        return result
    }
}