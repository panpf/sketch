package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest

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
    val key: String

    /**
     * Apply the transformation to [input] and return the transformed [Bitmap].
     *
     * @param request [ImageRequest].
     * @param input The input [Bitmap] to transform.
     * @return The transformed [Bitmap].
     */
    suspend fun transform(sketch: Sketch, request: ImageRequest, input: Bitmap): TransformResult?
}


fun List<Transformation>?.merge(other: List<Transformation>?): List<Transformation>? =
    if (this != null) {
        if (other != null) {
            this.plus(other).distinctBy {
                it.key
            }
        } else {
            this
        }
    } else {
        other
    }