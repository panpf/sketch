package com.github.panpf.sketch3.common

interface Interceptor<T: Any, R: Any> {

    fun intercept(chain: Chain<T, R>): R

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

        suspend fun proceed(request: T): R
    }
}