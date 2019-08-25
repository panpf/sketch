package me.panpf.sketch.sample.bean

import com.google.gson.annotations.SerializedName

class BaiduImageSearchResult {
    @SerializedName("listNum")
    var total: Int = 0

    @SerializedName("data")
    var imageList: List<BaiduImage>? = null
}

class BaiduImage {

    @SerializedName("thumbURL")
    val thumbURL: String? = null

    @SerializedName("middleURL")
    val middleURL: String? = null

    @SerializedName("hoverURL")
    val hoverURL: String? = null

    @SerializedName("replaceUrl")
    private val replaceUrlList: List<ReplaceUrl>? = null

    @SerializedName("width")
    val width: Int = 0

    @SerializedName("height")
    val height: Int = 0

    val url: String?
        get() {
            return if (hoverURL != middleURL && hoverURL != thumbURL) {
                hoverURL
            } else {
                replaceUrlList?.lastOrNull()?.objUrl
            }
        }
}

class ReplaceUrl {
    @SerializedName("ObjURL")
    var objUrl: String? = null
}