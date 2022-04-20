package com.github.panpf.sketch.resize

import android.content.Context
import androidx.annotation.Keep
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import org.json.JSONObject

fun longImageScale(
    longImage: Scale,
    other: Scale,
): LongImageScaleDecider =
    LongImageScaleDecider(longImage, other)

@Keep
data class LongImageScaleDecider constructor(
    private val longImage: Scale,
    private val other: Scale,
) : ScaleDecider {

    @Keep
    constructor(jsonObject: JSONObject) : this(
        Scale.valueOf(jsonObject.getString("longImage")),
        Scale.valueOf(jsonObject.getString("other")),
    )

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("longImage", longImage.name)
            put("other", other.name)
        }

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): ScaleDecider = LongImageScaleDecider(
        longImage = exifOrientationHelper.addToScale(longImage, imageSize),
        other = exifOrientationHelper.addToScale(other, imageSize),
    )

    override val key: String by lazy { toString() }

    override fun get(
        context: Context, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        val longImageDecider = context.sketch.longImageDecider
        return if (longImageDecider.isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight))
            longImage else other
    }

    override fun toString(): String =
        "LongImageScaleDecider($longImage,$other)"
}