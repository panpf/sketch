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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.decode.Resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.decode.Resize.Scale.CENTER_CROP
import com.github.panpf.sketch.decode.Resize.Scope.All
import com.github.panpf.sketch.util.format

data class Resize constructor(
    val width: Int,
    val height: Int,
    val precision: Precision = KEEP_ASPECT_RATIO,
    val scale: Scale = CENTER_CROP,
    val scope: Scope = All
) {

    val cacheKey: String = "Resize(${width}x${height},${scale},${precision},${scope})"

    enum class Precision {
        /**
         * Even if the size of the original image is smaller than [Resize], you will get a [Bitmap] with the same size as [Resize]
         */
        EXACTLY,

        /**
         * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
         */
        KEEP_ASPECT_RATIO,
    }

    enum class Scale {
        START_CROP,
        CENTER_CROP,
        END_CROP,
        FILL,
    }

    sealed interface Scope {

        /**
         * Resize works on all image.
         */
        object All : Scope {
            override fun toString(): String = "ALL"
        }

        /**
         * Resize only works on long image. How to determine the long image please see [isLongImage]
         */
        data class OnlyLongImage(private val minAspectRatio: Float = 1.5f) : Scope {

            fun isLongImage(
                imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
            ): Boolean {
                val imageAspectRatio =
                    (imageWidth.toFloat() / imageHeight.toFloat()).format(1)
                val resizeAspectRatio = (resizeWidth.toFloat() / resizeHeight.toFloat()).format(1)
                return isLongImageByAspectRatio(imageAspectRatio, resizeAspectRatio)
            }

            fun isLongImageByAspectRatio(
                imageAspectRatio: Float,
                resizeAspectRatio: Float
            ): Boolean {
                val maxAspectRatio = resizeAspectRatio.coerceAtLeast(imageAspectRatio)
                val minAspectRatio = resizeAspectRatio.coerceAtMost(imageAspectRatio)
                return maxAspectRatio > minAspectRatio * minAspectRatio
            }

            override fun toString(): String = "OnlyLongImage(minAspectRatio=$minAspectRatio)"
        }
    }
}