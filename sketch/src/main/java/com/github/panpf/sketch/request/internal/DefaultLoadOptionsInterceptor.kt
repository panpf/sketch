package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class DefaultLoadOptionsInterceptor(
    private val defaultLoadOptions: LoadOptions?
) : RequestInterceptor<LoadRequest, LoadData> {

    @MainThread
    override suspend fun intercept(chain: Chain<LoadRequest, LoadData>): LoadData {
        val request = if (defaultLoadOptions?.isEmpty() == true) {
            chain.request.newLoadRequest {
                options(defaultLoadOptions, requestFirst = true)
            }
        } else {
            chain.request
        }
        return chain.proceed(request)
    }

    override fun toString(): String = "DefaultLoadOptionsInterceptor"
}