package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Intercept the execution of [Bitmap] decode, you can change the output, register to [ComponentRegistry] to take effect
 */
fun interface BitmapDecodeInterceptor {

    @WorkerThread
    suspend fun intercept(chain: Chain): BitmapDecodeResult

    interface Chain {

        val sketch: Sketch

        val request: ImageRequest

        val requestContext: RequestContext

        val fetchResult: FetchResult?

        /**
         * Continue executing the chain.
         */
        @WorkerThread
        suspend fun proceed(): BitmapDecodeResult
    }
}