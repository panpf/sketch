package com.github.panpf.sketch

fun Image.findLeafImage(): Image = if (this is ImageWrapper) image.findLeafImage() else this

open class ImageWrapper(val image: Image) : Image by image {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageWrapper) return false
        return image == other.image
    }

    override fun hashCode(): Int = image.hashCode()

    override fun toString(): String = "ImageWrapper(image=$image)"
}