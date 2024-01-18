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
package com.github.panpf.sketch.sample.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GiphySearchResponse(
    @SerialName("data") val dataList: List<GiphyGif>?
)

@Serializable
class GiphyGif(
    @SerialName("images") val images: GiphyImages
)

@Serializable
class GiphyImages(
    @SerialName("original") val original: GiphyImage,
    @SerialName("fixed_width") val fixedWidth: GiphyImage,
    @SerialName("preview_gif") val previewGif: GiphyImage,
    @SerialName("preview_webp") val previewWebp: GiphyImage,
)

@Serializable
class GiphyImage(
    @SerialName("url") val url: String,
    @SerialName("preview") val preview: String? = null,
    @SerialName("size") val size: Long,
    @SerialName("webp_size") val webpSize: Long? = null,
    @SerialName("width") val width: Long,
    @SerialName("height") val height: Long,
    @SerialName("webp") private val webpUrl: String? = null,
    @SerialName("frames") private val frames: Int? = null,
) {

    val downloadUrl: String by lazy {
        Regex("media[\\d].giphy.com").replace(url, "i.giphy.com")
    }

    val webpDownloadUrl: String? by lazy {
        if (webpUrl != null) Regex("media[\\d].giphy.com").replace(webpUrl, "i.giphy.com") else null
    }
}