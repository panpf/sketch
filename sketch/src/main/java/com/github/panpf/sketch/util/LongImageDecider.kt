package com.github.panpf.sketch.util

interface LongImageDecider {

    fun isLongImage(
        imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int
    ): Boolean
}

open class DefaultLongImageDecider(
    val ratioMultipleWhenSameDirection: Float = 2.5f,
    val ratioMultipleWhenNotSameDirection: Float = 5.0f,
) : LongImageDecider {

    override fun isLongImage(
        imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int
    ): Boolean {
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
        val targetAspectRatio = targetWidth.toFloat().div(targetHeight).format(1)
        val maxAspectRatio = targetAspectRatio.coerceAtLeast(imageAspectRatio)
        val minAspectRatio = targetAspectRatio.coerceAtMost(imageAspectRatio)
        return when {
            (imageWidth >= imageHeight && targetWidth >= targetHeight)
                    || (imageWidth <= imageHeight && targetWidth <= targetHeight) -> {
                maxAspectRatio >= (minAspectRatio * ratioMultipleWhenSameDirection)
            }
            else -> {
                maxAspectRatio >= (minAspectRatio * ratioMultipleWhenNotSameDirection)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultLongImageDecider

        if (ratioMultipleWhenSameDirection != other.ratioMultipleWhenSameDirection) return false
        if (ratioMultipleWhenNotSameDirection != other.ratioMultipleWhenNotSameDirection) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ratioMultipleWhenSameDirection.hashCode()
        result = 31 * result + ratioMultipleWhenNotSameDirection.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultLongImageDecider(sameDirection=$ratioMultipleWhenSameDirection,notSameDirection=$ratioMultipleWhenNotSameDirection)"
    }
}