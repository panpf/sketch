package com.github.panpf.sketch.resize

import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size

fun fixedScale(precision: Scale): FixedScaleDecider = FixedScaleDecider(precision)

/**
 * Always return specified precision
 */
data class FixedScaleDecider(private val scale: Scale) : ScaleDecider {

    override val key: String by lazy { "Fixed($scale)" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        return scale
    }

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): FixedScaleDecider {
        return FixedScaleDecider(exifOrientationHelper.addToScale(scale, imageSize))
    }

    override fun toString(): String {
        return "FixedScaleDecider(scale=$scale)"
    }
}