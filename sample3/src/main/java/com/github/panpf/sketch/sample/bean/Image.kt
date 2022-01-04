package com.github.panpf.sketch.sample.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class Image(
    @SerialName("normalQualityUrl") val normalQualityUrl: String,
    @SerialName("rawQualityUrl") val rawQualityUrl: String
) : Parcelable
