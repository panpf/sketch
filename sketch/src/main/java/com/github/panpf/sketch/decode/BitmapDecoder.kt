package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Decode [Bitmap] from [DataSource].
 */
fun interface BitmapDecoder {

    /**
     * Decode [Bitmap] from [DataSource] and wrap it as a [BitmapDecodeResult] return.
     */
    @WorkerThread
    suspend fun decode(): BitmapDecodeResult

    /**
     * [Factory] will be registered in [ComponentRegistry], and will traverse [Factory]
     * to create [BitmapDecoder] when it needs decode [Bitmap]
     */
    fun interface Factory {

        /**
         * If the current [Factory]'s [BitmapDecoder] can decode [Bitmap] from the current [fetchResult],
         * create a [BitmapDecoder] and return it, otherwise return null
         */
        fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder?
    }
}
