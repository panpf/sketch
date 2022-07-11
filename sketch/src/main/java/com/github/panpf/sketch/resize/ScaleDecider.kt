package com.github.panpf.sketch.resize

import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.Size

/**
 * Decide which scale to use
 */
interface ScaleDecider : JsonSerializable {

    val key: String

    fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale

    fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): ScaleDecider
}