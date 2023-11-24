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
package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.fetch.newAssetUri

object TestAssets {
    val SAMPLE_BMP_URI = newAssetUri("sample.bmp")
    val SAMPLE_HEIC_URI = newAssetUri("sample.heic")
    val SAMPLE_JPEG_URI = newAssetUri("sample.jpeg")
    val SAMPLE_PNG_URI = newAssetUri("sample.png")
    val SAMPLE_WEBP_URI = newAssetUri("sample.webp")
    val SAMPLE_ANIM_GIF_URI = newAssetUri("sample_anim.gif")
    val SAMPLE_ANIM_WEBP_URI = newAssetUri("sample_anim.webp")
    val SAMPLE_ANIM_HEIf_URI = newAssetUri("sample_anim.heif")
    val ERROR_URI = newAssetUri("error.jpeg")
}