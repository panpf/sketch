package com.github.panpf.sketch.decode.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject

class ExifOrientationTransformed(val exifOrientation: Int) : Transformed {

    override val key: String by lazy {
        "ExifOrientationTransformed(${exifOrientationName(exifOrientation)})"
    }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExifOrientationTransformed

        if (exifOrientation != other.exifOrientation) return false

        return true
    }

    override fun hashCode(): Int {
        return exifOrientation
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<ExifOrientationTransformed> {
        override fun toJson(t: ExifOrientationTransformed): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("exifOrientation", exifOrientation)
                }
            }

        override fun fromJson(jsonObject: JSONObject): ExifOrientationTransformed =
            ExifOrientationTransformed(
                jsonObject.getInt("exifOrientation")
            )
    }
}

fun List<Transformed>.getExifOrientationTransformed(): ExifOrientationTransformed? =
    find { it is ExifOrientationTransformed } as ExifOrientationTransformed?