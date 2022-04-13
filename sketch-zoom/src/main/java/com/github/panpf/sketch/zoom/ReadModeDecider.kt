package com.github.panpf.sketch.zoom

import com.github.panpf.sketch.util.format

class DefaultReadModeDecider(
    private val minDifferenceOfAspectRatio: Float = 3f
) : ReadModeDecider {

    override fun should(
        imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int
    ): Boolean {
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
        val viewAspectRatio = viewWidth.toFloat().div(viewHeight).format(1)
        val maxAspectRatio = viewAspectRatio.coerceAtLeast(imageAspectRatio)
        val minAspectRatio = viewAspectRatio.coerceAtMost(imageAspectRatio)
        return maxAspectRatio >= (minAspectRatio * minDifferenceOfAspectRatio)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultReadModeDecider

        if (minDifferenceOfAspectRatio != other.minDifferenceOfAspectRatio) return false

        return true
    }

    override fun hashCode(): Int {
        return minDifferenceOfAspectRatio.hashCode()
    }

    override fun toString(): String {
        return "DefaultReadModeDecider($minDifferenceOfAspectRatio)"
    }
}

interface ReadModeDecider {

    fun should(imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int): Boolean
}