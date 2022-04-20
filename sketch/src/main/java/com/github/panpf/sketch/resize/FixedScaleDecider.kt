package com.github.panpf.sketch.resize

import android.content.Context
import androidx.annotation.Keep
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size
import org.json.JSONObject

fun fixedScale(precision: Scale): FixedScaleDecider = FixedScaleDecider(precision)

/**
 * Always return specified precision
 */
@Keep
data class FixedScaleDecider(private val scale: Scale) : ScaleDecider {

    override val key: String by lazy { "FixedScaleDecider($scale)" }

    override fun get(
        context: Context, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        return scale
    }

    @Keep
    constructor(jsonObject: JSONObject) : this(Scale.valueOf(jsonObject.getString("scale")))

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("scale", scale.name)
        }

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): ScaleDecider {
        return FixedScaleDecider(exifOrientationHelper.addToScale(scale, imageSize))
    }

    override fun toString(): String = "FixedScaleDecider($scale)"
}