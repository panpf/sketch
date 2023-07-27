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

import android.graphics.Bitmap

enum class Precision {

    // todo added SMALLER_SIZE, and use default value of Resize to determine the size

    /**
     * Try to keep the number of pixels of the returned image smaller than resize. A 10% margin of error is allowed
     */
    LESS_PIXELS,

    /**
     * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
     */
    SAME_ASPECT_RATIO,

    /**
     * The size of the [Bitmap] returned is exactly the same as [Resize]
     */
    EXACTLY,
}