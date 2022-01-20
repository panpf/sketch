package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class DefaultOptionsLoadInterceptor(
    private val defaultLoadOptions: LoadOptions?
) : RequestInterceptor<LoadRequest, LoadData> {
    override suspend fun intercept(chain: Chain<LoadRequest, LoadData>): LoadData {
        val request = if (defaultLoadOptions?.isEmpty() == true) {
            chain.request.newLoadRequest {
                // todo 改为 request 优先模式
                options(defaultLoadOptions)
            }
        } else {
            chain.request
        }
        return chain.proceed(request)
    }
}