package com.github.panpf.sketch.decode.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject

class InSampledTransformed(val inSampleSize: Int) : Transformed {

    override val key: String by lazy { "InSampledTransformed($inSampleSize)" }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InSampledTransformed

        if (inSampleSize != other.inSampleSize) return false

        return true
    }

    override fun hashCode(): Int {
        return inSampleSize
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<InSampledTransformed> {
        override fun toJson(t: InSampledTransformed): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("inSampleSize", inSampleSize)
                }
            }

        override fun fromJson(jsonObject: JSONObject): InSampledTransformed =
            InSampledTransformed(
                jsonObject.getInt("inSampleSize")
            )
    }
}

fun List<Transformed>.getInSampledTransformed(): InSampledTransformed? =
    find { it is InSampledTransformed } as InSampledTransformed?