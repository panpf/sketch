package com.github.panpf.sketch3.common

import com.github.panpf.sketch3.Sketch3

interface Interceptor<T : ImageRequest, R : Any> {

    suspend fun intercept(sketch3: Sketch3, chain: Chain<T, R>): R

    interface Chain<T : ImageRequest, R> {

        val request: T

        suspend fun proceed(sketch3: Sketch3, request: T): R
    }
}