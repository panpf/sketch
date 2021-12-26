package com.github.panpf.sketch.common

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch

interface Interceptor<T : ImageRequest, R : Any> {

    @WorkerThread
    suspend fun intercept(
        sketch: Sketch,
        chain: Chain<T, R>,
        httpFetchProgressListener: ProgressListener<ImageRequest>?
    ): R

    interface Chain<T : ImageRequest, R> {

        val request: T

        @WorkerThread
        suspend fun proceed(
            sketch: Sketch,
            request: T,
            httpFetchProgressListener: ProgressListener<ImageRequest>?
        ): R
    }
}