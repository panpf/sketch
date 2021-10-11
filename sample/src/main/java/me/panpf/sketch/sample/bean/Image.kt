package me.panpf.sketch.sample.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Image(
    @SerializedName("normalQualityUrl") val normalQualityUrl: String,
    @SerializedName("rawQualityUrl") val rawQualityUrl: String
) : Parcelable
