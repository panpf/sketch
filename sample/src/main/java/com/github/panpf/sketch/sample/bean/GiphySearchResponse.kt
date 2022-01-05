package com.github.panpf.sketch.sample.bean

import com.github.panpf.assemblyadapter.recycler.DiffKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class GiphySearchResponse(
    @SerialName("data") val dataList: List<GiphyGif>?
)

@Serializable
class GiphyGif(
    @SerialName("images") val images: GiphyImages
) : DiffKey {
    @Transient
    override val diffKey: String = "GiphyGif-${images.original.url}"
}

@Serializable
class GiphyImages(
    @SerialName("original") val original: GiphyImage,
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