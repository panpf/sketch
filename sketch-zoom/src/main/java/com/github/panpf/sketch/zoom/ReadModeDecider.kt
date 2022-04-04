package com.github.panpf.sketch.zoom

class DefaultReadModeDecider(
    private val differenceByWidth: Float = 3f,
    private val differenceByHeight: Float = 2f
) : ReadModeDecider {

    override fun should(imageWidth: Int, imageHeight: Int): Boolean {
        return imageWidth >= imageHeight * differenceByWidth
                || imageHeight >= imageWidth * differenceByHeight
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultReadModeDecider

        if (differenceByWidth != other.differenceByWidth) return false
        if (differenceByHeight != other.differenceByHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = differenceByWidth.hashCode()
        result = 31 * result + differenceByHeight.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultReadModeDecider(differenceByWidth=$differenceByWidth, differenceByHeight=$differenceByHeight)"
    }
}

interface ReadModeDecider {

    fun should(imageWidth: Int, imageHeight: Int): Boolean
}