package com.github.panpf.sketch.decode.resize

import android.graphics.Bitmap

enum class Precision {
    /**
     * The size of the [Bitmap] returned is exactly the same as [Resize]
     */
    EXACTLY,

    /**
     * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
     */
    KEEP_ASPECT_RATIO,

    /**
     * As long as the bitmap returned has fewer pixels than resize, it is ok
     */
    LESS_PIXELS,
}