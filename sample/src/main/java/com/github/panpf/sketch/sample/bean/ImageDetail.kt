package com.github.panpf.sketch.sample.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class ImageDetail(
    @SerialName("url") val url: String,
    @SerialName("middenUrl") val middenUrl: String?,
    @SerialName("placeholderImageMemoryKey") val placeholderImageMemoryKey: String?,
) : Parcelable {
    val firstMiddenUrl: String = middenUrl ?: url
}
