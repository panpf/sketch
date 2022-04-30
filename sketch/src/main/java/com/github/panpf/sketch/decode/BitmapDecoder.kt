package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras

/**
 * A [BitmapDecoder] converts a [DataSource] into a [Bitmap].
 */
interface BitmapDecoder {

    @WorkerThread
    suspend fun decode(): BitmapDecodeResult

    fun interface Factory {

        fun create(
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): BitmapDecoder?
    }
}
