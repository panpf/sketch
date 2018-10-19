package me.panpf.sketch.sample.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Image(val normalQualityUrl: String, val rawQualityUrl: String) : Parcelable
