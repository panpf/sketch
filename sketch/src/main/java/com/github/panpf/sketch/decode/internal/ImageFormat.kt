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
package com.github.panpf.sketch.decode.internal

enum class ImageFormat(val mimeType: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp"),
    GIF("image/gif"),
    BMP("image/bmp"),
    HEIC("image/heic"),
    HEIF("image/heif"),
    ;
}

fun mimeTypeToImageFormat(mimeType: String?): ImageFormat? = when {
    ImageFormat.JPEG.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.JPEG
    ImageFormat.PNG.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.PNG
    ImageFormat.WEBP.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.WEBP
    ImageFormat.GIF.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.GIF
    ImageFormat.BMP.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.BMP
    ImageFormat.HEIC.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.HEIC
    ImageFormat.HEIF.mimeType.equals(mimeType, ignoreCase = true) -> ImageFormat.HEIF
    else -> null
}