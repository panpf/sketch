package me.panpf.sketch.sample.bean

import com.google.gson.annotations.SerializedName

class TenorSearchResponse(@SerializedName("results") val dataList: List<TenorData>?)

class TenorData(@SerializedName("media") private val media: List<TenorMedia>?) {
    val gifMedia: TenorMediaData?
        get() = media?.get(0)?.gifMedia
    val tinyGifMedia: TenorMediaData?
        get() = media?.get(0)?.tinyGifMedia
}

class TenorMedia(@SerializedName("gif") val gifMedia: TenorMediaData, @SerializedName("tinygif") val tinyGifMedia: TenorMediaData)

class TenorMediaData(@SerializedName("url") val url: String
                     , @SerializedName("dims") private val dims: Array<Int>
                     , @SerializedName("preview") val preview: String
                     , @SerializedName("size") val size: Long) {
    val width: Int
        get() = dims[0]
    val height: Int
        get() = dims[1]
}