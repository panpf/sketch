package com.github.panpf.sketch.sample.bean

import com.github.panpf.assemblyadapter.recycler.DiffKey
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
): DiffKey {
    override val diffKey = "PexelsPhoto:$id"
}

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