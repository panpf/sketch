package com.github.panpf.sketch.zoom

import android.content.Context
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.util.Size

data class Scales(
    /**
     * Minimum scale
     */
    val min: Float,

    /**
     * Maximum scale
     */
    val max: Float,

    /**
     * Maximum initial scaling ratio
     */
    val init: Float,

    /**
     * You can see the full scale of the picture
     */
    val full: Float,

    /**
     * Make the width or height fill the screen's zoom ratio
     */
    val fill: Float,

    /**
     * The ability to display images in one-to-one scale to their true size
     */
    val origin: Float,

    /**
     * Double-click to scale the desired scale group
     */
    val doubleClicks: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scales

        if (min != other.min) return false
        if (max != other.max) return false
        if (init != other.init) return false
        if (full != other.full) return false
        if (fill != other.fill) return false
        if (origin != other.origin) return false
        if (!doubleClicks.contentEquals(other.doubleClicks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + init.hashCode()
        result = 31 * result + full.hashCode()
        result = 31 * result + fill.hashCode()
        result = 31 * result + origin.hashCode()
        result = 31 * result + doubleClicks.contentHashCode()
        return result
    }
}

interface ScalesFactory {
    fun create(
        context: Context,
        viewSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        imageSize: Size,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): Scales
}