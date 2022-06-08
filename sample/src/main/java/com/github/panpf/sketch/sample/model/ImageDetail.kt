package com.github.panpf.sketch.sample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ImageDetail constructor(
    @SerialName("position") val position: Int,
    @SerialName("url") val url: String,
    @SerialName("middenUrl") val middenUrl: String?,
    @SerialName("placeholderImageMemoryCacheKey") val placeholderImageMemoryCacheKey: String?,
) : Parcelable
