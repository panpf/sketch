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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.util.Size

data class ImageInfo constructor(
    val size: Size,
    val mimeType: String,
    @ExifOrientation val exifOrientation: Int,
) {

    val width: Int get() = size.width
    val height: Int get() = size.height

    constructor(
        width: Int,
        height: Int,
        mimeType: String,
        @ExifOrientation exifOrientation: Int,
    ) : this(Size(width, height), mimeType, exifOrientation)

    override fun toString(): String {
        val exifOrientationName = ExifOrientation.name(exifOrientation)
        return "ImageInfo(size=$size, mimeType='$mimeType', exifOrientation=$exifOrientationName)"
    }

    fun toShortString(): String =
        "ImageInfo(${width}x$height,'$mimeType',${ExifOrientation.name(exifOrientation)})"
}