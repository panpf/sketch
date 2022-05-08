package com.github.panpf.sketch.util

interface LongImageDecider {

    fun isLongImage(
        imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int
    ): Boolean
}

open class DefaultLongImageDecider(
    val smallRatioMultiple: Float = 2.5f,
    val bigRatioMultiple: Float = 5.0f,
) : LongImageDecider {

    override fun isLongImage(
        imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int
    ): Boolean {
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(2)
        val targetAspectRatio = targetWidth.toFloat().div(targetHeight).format(2)
        val maxAspectRatio = targetAspectRatio.coerceAtLeast(imageAspectRatio)
        val minAspectRatio = targetAspectRatio.coerceAtMost(imageAspectRatio)
        val ratioMultiple = if (imageAspectRatio == 1.0f || targetAspectRatio == 1.0f) {
            // Either one is a square
            smallRatioMultiple
        } else if (imageAspectRatio > 1.0f && targetAspectRatio > 1.0f || (imageAspectRatio < 1.0f && targetAspectRatio < 1.0f)) {
            // They go in the same direction
            smallRatioMultiple
        } else {
            // They don't go in the same direction
            bigRatioMultiple
        }
        return maxAspectRatio >= (minAspectRatio * ratioMultiple)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultLongImageDecider

        if (smallRatioMultiple != other.smallRatioMultiple) return false
        if (bigRatioMultiple != other.bigRatioMultiple) return false

        return true
    }

    override fun hashCode(): Int {
        var result = smallRatioMultiple.hashCode()
        result = 31 * result + bigRatioMultiple.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultLongImageDecider(smallRatioMultiple=$smallRatioMultiple,bigRatioMultiple=$bigRatioMultiple)"
    }
}