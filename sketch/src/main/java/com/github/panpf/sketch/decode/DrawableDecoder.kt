package com.github.panpf.sketch.decode

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras

interface DrawableDecoder {

    @WorkerThread
    suspend fun decode(): DrawableDecodeResult

    fun interface Factory {

        fun create(
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult,
        ): DrawableDecoder?
    }
}