package com.github.panpf.sketch.decode

import com.github.panpf.sketch.request.LoadRequest

interface ImageDecodeInterceptor {

    fun intercept(chain: Chain): DecodeResult

    interface Chain {
        fun request(): LoadRequest

        fun proceed(request: LoadRequest): DecodeResult
    }
}