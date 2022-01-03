/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.widget.ImageView.ScaleType

data class Resize constructor(
    val width: Int,
    val height: Int,
    val mode: Mode = DEFAULT_MODE,
    val scaleType: ScaleType = DEFAULT_SCALE_TYPE,
    /**
     * Only applies to [Resize.Mode.THUMBNAIL_MODE]
     */
    val minAspectRatio: Float = DEFAULT_MIN_ASPECT_RATIO,
) {

    val cacheKey: String =
        "Resize(${width}x${height},${scaleType},${mode}${if (mode == Mode.THUMBNAIL_MODE) "-$minAspectRatio" else ""})"

    companion object {
        val DEFAULT_MODE: Mode = Mode.EXACTLY_SAME
        val DEFAULT_SCALE_TYPE: ScaleType = ScaleType.FIT_CENTER
        val DEFAULT_MIN_ASPECT_RATIO: Float = 1.5f
    }

    enum class Mode {
        /**
         * Even if the size of the original image is smaller than [Resize], you will get a [Bitmap] with the same size as [Resize]
         */
        EXACTLY_SAME,

        /**
         * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
         */
        ASPECT_RATIO_SAME,

        /**
         * If the difference between the aspect ratio of resize and the aspect ratio of image exceeds [minAspectRatio],
         * a portion of the original image will be captured based on the [width],[height],[scaleType] attributes.
         * This mode is suitable for displaying thumbnails of super-long images in nine squares
         */
        THUMBNAIL_MODE,
    }
}