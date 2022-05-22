package com.github.panpf.sketch.resize

/**
 * Which part of the original image to keep when [Precision] is [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
 */
enum class Scale {

    /**
     * Keep the start of the original image
     */
    START_CROP,

    /**
     * Keep the center of the original image
     */
    CENTER_CROP,

    /**
     * Keep the end of the original image
     */
    END_CROP,

    /**
     * Keep the all of the original image, but deformed
     */
    FILL,
}