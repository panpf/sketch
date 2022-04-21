package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.Size
import org.json.JSONObject

fun longImageScale(
    longImage: Scale,
    other: Scale,
): LongImageScaleDecider =
    LongImageScaleDecider(longImage, other)

data class LongImageScaleDecider constructor(
    private val longImage: Scale,
    private val other: Scale,
) : ScaleDecider {

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): ScaleDecider = LongImageScaleDecider(
        longImage = exifOrientationHelper.addToScale(longImage, imageSize),
        other = exifOrientationHelper.addToScale(other, imageSize),
    )

    override val key: String by lazy { toString() }

    override fun get(
        sketch: Sketch, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        val longImageDecider = sketch.longImageDecider
        return if (longImageDecider.isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight))
            longImage else other
    }

    override fun toString(): String =
        "LongImageScaleDecider($longImage,$other)"

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<LongImageScaleDecider> {
        override fun toJson(t: LongImageScaleDecider): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("longImage", longImage.name)
                    put("other", other.name)
                }
            }

        override fun fromJson(jsonObject: JSONObject): LongImageScaleDecider =
            LongImageScaleDecider(
                Scale.valueOf(jsonObject.getString("longImage")),
                Scale.valueOf(jsonObject.getString("other")),
            )
    }
}