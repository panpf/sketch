package com.github.panpf.sketch.sample.bean

import android.os.Parcelable
import com.github.panpf.assemblyadapter.recycler.DiffKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Photo constructor(
    @SerialName("url") val url: String,
    @SerialName("thumbnailUrl") val thumbnailUrl: String?,
    @SerialName("middenUrl") val middenUrl: String?,
    @SerialName("width") val width: Int?,
    @SerialName("height") val height: Int?,
) : Parcelable, DiffKey {
    override val diffKey: String = toString()

    val firstThumbnailUrl: String = thumbnailUrl ?: middenUrl ?: url

    val firstMiddenUrl: String = middenUrl ?: url
}