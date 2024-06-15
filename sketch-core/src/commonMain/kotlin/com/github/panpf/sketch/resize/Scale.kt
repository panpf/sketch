/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

fun Scale.reverse(): Scale {
    return when (this) {
        Scale.START_CROP -> Scale.END_CROP
        Scale.CENTER_CROP -> Scale.CENTER_CROP
        Scale.END_CROP -> Scale.START_CROP
        Scale.FILL -> Scale.FILL
    }
}