package me.panpf.sketch.sample.bean

import com.google.gson.annotations.SerializedName

class BaiduImage {

    @SerializedName("replaceUrl")
    private val replaceUrlList: List<ReplaceUrl>? = null

    @SerializedName("width")
    val width: Int = 0

    @SerializedName("height")
    val height: Int = 0

    val url: String?
        get() = if (replaceUrlList != null && replaceUrlList.size > 0) replaceUrlList[0].objUrl else null

    class ReplaceUrl {
        @SerializedName("ObjURL")
        var objUrl: String? = null
    }
}
