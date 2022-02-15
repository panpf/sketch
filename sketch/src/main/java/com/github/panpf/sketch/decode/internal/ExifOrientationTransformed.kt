package com.github.panpf.sketch.decode.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.Transformed
import org.json.JSONObject

@Keep
class ExifOrientationTransformed(val exifOrientation: Int) : Transformed {
    override val key: String =
        "ExifOrientationTransformed(${exifOrientationName(exifOrientation)})"
    override val cacheResultToDisk: Boolean = true

    @Keep
    constructor(jsonObject: JSONObject) : this(jsonObject.getInt("exifOrientation"))

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("exifOrientation", exifOrientation)
        }

    override fun toString(): String = key
}

fun List<Transformed>.getExifOrientationTransformed(): ExifOrientationTransformed? =
    find { it is ExifOrientationTransformed } as ExifOrientationTransformed?