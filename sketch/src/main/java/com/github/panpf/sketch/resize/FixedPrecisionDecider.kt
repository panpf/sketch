package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject

fun fixedPrecision(precision: Precision): FixedPrecisionDecider = FixedPrecisionDecider(precision)

/**
 * Always return specified precision
 */
data class FixedPrecisionDecider(private val precision: Precision) : PrecisionDecider {

    override val key: String by lazy { "FixedPrecisionDecider($precision)" }

    override fun get(
        sketch: Sketch, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision {
        return precision
    }

    override fun toString(): String = key

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<FixedPrecisionDecider> {
        override fun toJson(t: FixedPrecisionDecider): JSONObject =
            JSONObject().apply {
                put("precision", t.precision.name)
            }

        override fun fromJson(jsonObject: JSONObject): FixedPrecisionDecider =
            FixedPrecisionDecider(
                Precision.valueOf(jsonObject.getString("precision")),
            )
    }
}