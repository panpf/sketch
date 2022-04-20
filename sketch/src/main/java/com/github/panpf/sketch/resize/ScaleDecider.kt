package com.github.panpf.sketch.resize

import android.content.Context
import androidx.annotation.Keep
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size
import org.json.JSONObject

/**
 * Decide which scale to use
 */
@Keep
interface ScaleDecider {

    val key: String

    fun get(
        context: Context, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale

    fun serializationToJSON(): JSONObject

    fun addExifOrientation(exifOrientationHelper: ExifOrientationHelper, imageSize: Size): ScaleDecider
}