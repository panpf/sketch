/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.util.Size

data class ImageInfo constructor(
    val size: Size,
    val mimeType: String,
) {

    val width: Int get() = size.width
    val height: Int get() = size.height

    constructor(
        width: Int,
        height: Int,
        mimeType: String,
    ) : this(Size(width, height), mimeType)

    override fun toString(): String = "ImageInfo(size=$size, mimeType='$mimeType')"

    fun toShortString(): String = "ImageInfo(${width}x$height,'$mimeType')"
}