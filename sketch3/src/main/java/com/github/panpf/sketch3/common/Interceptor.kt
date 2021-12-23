package com.github.panpf.sketch3.common

import androidx.annotation.WorkerThread
import com.github.panpf.sketch3.Sketch3

interface Interceptor<T : ImageRequest, R : Any> {

    @WorkerThread
    suspend fun intercept(sketch3: Sketch3, chain: Chain<T, R>): R

    interface Chain<T : ImageRequest, R> {

        val request: T

        @WorkerThread
        suspend fun proceed(sketch3: Sketch3, request: T): R
    }
}