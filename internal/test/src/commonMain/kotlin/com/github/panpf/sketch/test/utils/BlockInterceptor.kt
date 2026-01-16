package com.github.panpf.sketch.test.utils

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.Interceptor
import kotlinx.coroutines.withContext

data class BlockInterceptor(
    val blockMillis: Long,
    override val sortWeight: Int
) : Interceptor {

    override val key: String? = null

    @MainThread
    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        withContext(chain.sketch.decodeTaskDispatcher) {
            block(blockMillis)
        }
        return chain.proceed(chain.request)
    }
}