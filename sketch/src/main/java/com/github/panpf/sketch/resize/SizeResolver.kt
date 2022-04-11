package com.github.panpf.sketch.resize

import androidx.annotation.MainThread
import com.github.panpf.sketch.util.Size

/**
 * An interface for measuring the target size for an image request.
 *
 * @see com.github.panpf.sketch.request.ImageRequest.Builder.resizeSize
 */
fun interface SizeResolver {

    /** Return the [Size] that the image should be loaded at. */
    @MainThread
    suspend fun size(): Size?
}