package me.panpf.sketch.sample.bean

import com.google.gson.annotations.SerializedName

class GiphySearchResponse(
    @SerializedName("data") val dataList: List<GiphyData>?
)

class GiphyData(
    @SerializedName("images") val media: GiphyImages?
)

class GiphyImages(
    @SerializedName("original") val original: GiphyImage,
    @SerializedName("preview_gif") val previewGif: GiphyImage,
    @SerializedName("preview_webp") val previewWebp: GiphyImage,
)

class GiphyImage(
    @SerializedName("url") val url: String,
    @SerializedName("preview") val preview: String,
    @SerializedName("size") val size: Long,
    @SerializedName("webp_size") val webpSize: Long,
    @SerializedName("width") val width: Long,
    @SerializedName("height") val height: Long,
    @SerializedName("webp") private val webpUrl: String,
    @SerializedName("frames") private val frames: Int,
) {
    private var downloadUrl: String? = null
    private var webpDownloadUrl: String? = null

    fun getDownloadUrl(): String {
        return downloadUrl ?: Regex("media[\\d].giphy.com").replace(url, "i.giphy.com").apply {
            downloadUrl = this
        }
    }

    fun getWebPDownloadUrl(): String {
        return webpDownloadUrl ?: Regex("media[\\d].giphy.com").replace(webpUrl, "i.giphy.com").apply {
            webpDownloadUrl = this
        }
    }
}