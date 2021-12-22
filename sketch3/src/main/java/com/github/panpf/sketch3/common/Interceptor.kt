package com.github.panpf.sketch3.common

import com.github.panpf.sketch3.Sketch3

interface Interceptor<T: Any, R: Any> {

    suspend fun intercept(sketch3: Sketch3, chain: Chain<T, R>): R

    interface Chain<T, R> {
        val request: T

//        val size: Size
//
//        /**
//         * Set the requested [Size] to load the image at.
//         *
//         * @param size The requested size for the image.
//         */
//        fun withSize(size: Size): Chain

        suspend fun proceed(sketch3: Sketch3, request: T): R
    }
}