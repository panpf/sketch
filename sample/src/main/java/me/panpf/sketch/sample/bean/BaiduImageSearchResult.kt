package me.panpf.sketch.sample.bean

import com.google.gson.annotations.SerializedName

class BaiduImageSearchResult {
    @SerializedName("listNum")
    var total: Int = 0

    @SerializedName("data")
    var imageList: List<BaiduImage>? = null
}
