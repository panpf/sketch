package com.github.panpf.sketch.decode.transform

import android.graphics.PixelFormat

/**
 * Represents the opacity of an image's pixels after applying an [AnimatedTransformation].
 */
enum class PixelOpacity {

    /**
     * Indicates that the [AnimatedTransformation] did not change the image's opacity.
     *
     * Return this unless you add transparent pixels to the image or remove all transparent
     * pixels in the image.
     */
    UNCHANGED,

    /**
     * Indicates that the [AnimatedTransformation] added transparent pixels to the image.
     */
    TRANSLUCENT,

    /**
     * Indicates that the [AnimatedTransformation] removed all transparent pixels in the image.
     */
    OPAQUE
}

internal val PixelOpacity.flag: Int
    get() = when (this) {
        PixelOpacity.UNCHANGED -> PixelFormat.UNKNOWN
        PixelOpacity.TRANSLUCENT -> PixelFormat.TRANSLUCENT
        PixelOpacity.OPAQUE -> PixelFormat.OPAQUE
    }
