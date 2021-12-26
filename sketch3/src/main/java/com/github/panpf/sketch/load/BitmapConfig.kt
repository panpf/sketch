package com.github.panpf.sketch.load

import android.graphics.Bitmap

class BitmapConfig(val config: Bitmap.Config) {

    companion object {
        /**
         * Use low-quality [Bitmap.Config] first. For example, use [Bitmap.Config.RGB_565] for JPEG format image, and use [Bitmap.Config.ARGB_4444] for other format of image.
         */
        @JvmStatic
        val LOW_QUALITY = BitmapConfig(Bitmap.Config.ARGB_8888)
    }
}