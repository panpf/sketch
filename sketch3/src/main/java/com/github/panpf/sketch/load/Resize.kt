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
package com.github.panpf.sketch.load

import android.widget.ImageView.ScaleType

data class Resize constructor(
    val width: Int,
    val height: Int,
    val scaleType: ScaleType = ScaleType.FIT_CENTER,
    val sizeMode: SizeMode = SizeMode.ASPECT_RATIO_SAME,
) {

    val cacheKey: String = "Resize(${width}x${height},${scaleType},${sizeMode})"

    enum class SizeMode {
        /**
         * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
         */
        ASPECT_RATIO_SAME,

        /**
         * Even if the size of the original image is smaller than [Resize], you will get a [android.graphics.Bitmap] with the same size as [Resize]
         */
        EXACTLY_SAME
    }
}