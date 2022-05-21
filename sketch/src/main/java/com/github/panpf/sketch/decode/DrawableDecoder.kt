package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Decode [Drawable] from [DataSource].
 */
fun interface DrawableDecoder {

    /**
     * Decode [Drawable] from [DataSource] and wrap it as a [DrawableDecodeResult] return.
     */
    @WorkerThread
    suspend fun decode(): DrawableDecodeResult

    /**
     * [Factory] will be registered in [ComponentRegistry], and will traverse [Factory]
     * to create [DrawableDecoder] when it needs decode [Drawable]
     */
    fun interface Factory {

        /**
         * If the current [Factory]'s [DrawableDecoder] can decode [Drawable] from the current [fetchResult],
         * create a [DrawableDecoder] and return it, otherwise return null
         */
        fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): DrawableDecoder?
    }
}