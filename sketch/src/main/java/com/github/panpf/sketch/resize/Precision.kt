package com.github.panpf.sketch.resize

import android.graphics.Bitmap

enum class Precision {
    /**
     * Try to keep the number of pixels of the returned image smaller than resize. A 10% margin of error is allowed
     */
    LESS_PIXELS,

    /**
     * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
     */
    // todo 改名为 SAME_ASPECT_RATIO
    SAME_ASPECT_RATIO,

    /**
     * The size of the [Bitmap] returned is exactly the same as [Resize]
     */
    EXACTLY,
}