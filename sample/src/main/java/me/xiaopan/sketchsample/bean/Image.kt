package me.xiaopan.sketchsample.bean

import android.os.Parcel
import android.os.Parcelable

class Image() : Parcelable {
    var regularUrl: String? = null
    var highDefinitionUrl: String? = null

    constructor(parcel: Parcel) : this() {
        regularUrl = parcel.readString()
        highDefinitionUrl = parcel.readString()
    }

    constructor(regularUrl: String, highDefinitionUrl: String) : this() {
        this.regularUrl = regularUrl
        this.highDefinitionUrl = highDefinitionUrl
    }

    override fun toString(): String {
        return String.format("Image{%s -> %s}", regularUrl, highDefinitionUrl)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regularUrl)
        parcel.writeString(highDefinitionUrl)
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
