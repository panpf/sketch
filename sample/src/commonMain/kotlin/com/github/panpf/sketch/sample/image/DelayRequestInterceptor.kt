package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.cache.internal.ResultCacheRequestInterceptor
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import kotlinx.coroutines.delay

data class DelayRequestInterceptor(
    val delay: Long,
    val onIntercept: (() -> Unit)? = null
) : RequestInterceptor {

    companion object {
        const val SORT_WEIGHT = ResultCacheRequestInterceptor.SORT_WEIGHT - 1
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        onIntercept?.invoke()
        delay(delay)
        return chain.proceed(chain.request)
    }
}