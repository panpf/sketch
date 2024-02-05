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
package com.github.panpf.sketch.sample.data.api.pexels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PexelsCurated(
    @SerialName("page") val page: Int,
    @SerialName("per_page") val pageSize: Int,
    @SerialName("photos") val photos: List<PexelsPhoto>,
)

@Serializable
class PexelsPhoto(
    @SerialName("id") val id: Int,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("url") val url: String,
    @SerialName("photographer") val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("photographer_id") val photographerId: Int,
    @SerialName("avg_color") val avgColor: String,
    @SerialName("src") val src: PexelsPhotoSrc,
)

@Serializable
class PexelsPhotoSrc(
    @SerialName("original") val original: String,
    @SerialName("large2x") val large2x: String,
    @SerialName("large") val large: String,
    @SerialName("medium") val medium: String,
    @SerialName("small") val small: String,
    @SerialName("portrait") val portrait: String,
    @SerialName("landscape") val landscape: String,
    @SerialName("tiny") val tiny: String,
)