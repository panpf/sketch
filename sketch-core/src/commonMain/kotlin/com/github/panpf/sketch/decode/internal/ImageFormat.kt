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

package com.github.panpf.sketch.decode.internal

/**
 * Image format
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.ImageFormatTest
 */
enum class ImageFormat(val mimeType: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp"),
    GIF("image/gif"),
    BMP("image/bmp"),
    HEIC("image/heic"), // Variants of HEIF format, dedicated to Apple devices
    HEIF("image/heif"),
    AVIF("image/avif"),
    SVG("image/svg+xml"),
    ;

    fun matched(mimeType: String?): Boolean = this.mimeType.equals(mimeType, ignoreCase = true)

    companion object {
        fun parseMimeType(mimeType: String?): ImageFormat? = when {
            JPEG.matched(mimeType) -> JPEG
            PNG.matched(mimeType) -> PNG
            WEBP.matched(mimeType) -> WEBP
            GIF.matched(mimeType) -> GIF
            BMP.matched(mimeType) -> BMP
            HEIC.matched(mimeType) -> HEIC
            HEIF.matched(mimeType) -> HEIF
            AVIF.matched(mimeType) -> AVIF
            SVG.matched(mimeType) -> SVG
            else -> null
        }
    }
}