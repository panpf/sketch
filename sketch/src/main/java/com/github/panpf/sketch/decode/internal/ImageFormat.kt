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

    companion object {
        @JvmStatic
        fun valueOfMimeType(mimeType: String?): ImageFormat? = when {
            JPEG.mimeType.equals(mimeType, ignoreCase = true) -> JPEG
            PNG.mimeType.equals(mimeType, ignoreCase = true) -> PNG
            WEBP.mimeType.equals(mimeType, ignoreCase = true) -> WEBP
            GIF.mimeType.equals(mimeType, ignoreCase = true) -> GIF
            BMP.mimeType.equals(mimeType, ignoreCase = true) -> BMP
            HEIC.mimeType.equals(mimeType, ignoreCase = true) -> HEIC
            HEIF.mimeType.equals(mimeType, ignoreCase = true) -> HEIF
            else -> null
        }
    }
}