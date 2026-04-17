package com.github.panpf.sketch.sample.image

import androidx.annotation.MainThread
import com.github.panpf.sketch.cache.internal.ResultCacheInterceptor
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.Interceptor
import kotlinx.coroutines.delay

data class DelayInterceptor(
    val delay: Long,
    val onIntercept: (() -> Unit)? = null
) : Interceptor {

    companion object {
        const val SORT_WEIGHT = ResultCacheInterceptor.SORT_WEIGHT - 1
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    @MainThread
    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        onIntercept?.invoke()
        delay(delay)
        return chain.proceed(chain.request)
    }
}