package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import org.json.JSONObject

fun longImageScale(
    longImage: Scale,
    other: Scale,
    longImageDecider: LongImageDecider = DefaultLongImageDecider()
): LongImageScaleDecider = LongImageScaleDecider(longImage, other, longImageDecider)

class LongImageScaleDecider constructor(
    val longImage: Scale,
    val otherImage: Scale,
    val longImageDecider: LongImageDecider = DefaultLongImageDecider(),
) : ScaleDecider {

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): LongImageScaleDecider = LongImageScaleDecider(
        longImage = exifOrientationHelper.addToScale(longImage, imageSize),
        otherImage = exifOrientationHelper.addToScale(otherImage, imageSize),
        longImageDecider = longImageDecider,
    )

    override val key: String by lazy { "LongImageScaleDecider(longImage=$longImage,otherImage=$otherImage),longImageDecider=$longImageDecider)" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        val isLongImage = longImageDecider
            .isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight)
        return if (isLongImage) longImage else otherImage
    }

    override fun toString(): String = key

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LongImageScaleDecider) return false
        if (longImage != other.longImage) return false
        if (otherImage != other.otherImage) return false
        if (longImageDecider != other.longImageDecider) return false
        return true
    }

    override fun hashCode(): Int {
        var result = longImage.hashCode()
        result = 31 * result + otherImage.hashCode()
        result = 31 * result + longImageDecider.hashCode()
        return result
    }

    @Keep
    class Serializer : JsonSerializer<LongImageScaleDecider> {
        override fun toJson(t: LongImageScaleDecider): JSONObject =
            JSONObject().apply {
                put("longImage", t.longImage.name)
                put("otherImage", t.otherImage.name)

                t.longImageDecider.also {
                    val serializerClass =
                        it.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                    val serializer = serializerClass.newInstance()
                    put("longImageDeciderSerializerClassName", serializerClass.name)
                    put("longImageDeciderContent", serializer.toJson(it))
                }
            }

        override fun fromJson(jsonObject: JSONObject): LongImageScaleDecider =
            LongImageScaleDecider(
                longImage = Scale.valueOf(jsonObject.getString("longImage")),
                otherImage = Scale.valueOf(jsonObject.getString("otherImage")),
                longImageDecider = jsonObject.getString("longImageDeciderSerializerClassName")
                    .let { Class.forName(it) }
                    .newInstance().asOrThrow<JsonSerializer<*>>()
                    .fromJson(jsonObject.getJSONObject("longImageDeciderContent"))!!
                    .asOrThrow(),
            )
    }
}