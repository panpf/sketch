package me.xiaopan.sketchsample.bean

import android.os.Parcel
import android.os.Parcelable

class Image() : Parcelable {
    var normalQualityUrl: String? = null
    var rawQualityUrl: String? = null

    constructor(parcel: Parcel) : this() {
        normalQualityUrl = parcel.readString()
        rawQualityUrl = parcel.readString()
    }

    constructor(regularUrl: String, highDefinitionUrl: String) : this() {
        this.normalQualityUrl = regularUrl
        this.rawQualityUrl = highDefinitionUrl
    }

    override fun toString(): String {
        return String.format("Image{%s -> %s}", normalQualityUrl, rawQualityUrl)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(normalQualityUrl)
        parcel.writeString(rawQualityUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}
