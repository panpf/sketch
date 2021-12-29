package com.github.panpf.sketch.load.transform

import android.graphics.Bitmap
import com.github.panpf.sketch.load.internal.LoadableRequest

/**
 * An interface for making transformations to an image's pixel data.
 */
interface Transformation {

    /**
     * The unique cache key for this transformation.
     *
     * The key is added to the image request's memory cache key and should contain any params that
     * are part of this transformation (e.g. size, scale, color, radius, etc.).
     */
    val cacheKey: String

    /**
     * Apply the transformation to [input] and return the transformed [Bitmap].
     *
     * @param request [LoadableRequest].
     * @param input The input [Bitmap] to transform.
     * @return The transformed [Bitmap].
     */
    suspend fun transform(request: LoadableRequest, input: Bitmap): Bitmap
}