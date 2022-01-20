package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class DefaultOptionsDisplayRequestInterceptor(
    private val defaultDisplayOptions: DisplayOptions?
) : RequestInterceptor<DisplayRequest, DisplayData> {

    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val request = if (defaultDisplayOptions?.isEmpty() == true) {
            chain.request.newDisplayRequest {
                // todo 改为 request 优先模式
                options(defaultDisplayOptions)
            }
        } else {
            chain.request
        }
        return chain.proceed(request)
    }
}