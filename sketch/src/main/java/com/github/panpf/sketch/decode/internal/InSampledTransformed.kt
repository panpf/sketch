package com.github.panpf.sketch.decode.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.Transformed
import org.json.JSONObject

@Keep
class InSampledTransformed(val inSampleSize: Int) : Transformed {
    override val key: String = "InSampledTransformed($inSampleSize)"
    override val cacheResultToDisk: Boolean = true

    @Keep
    constructor(jsonObject: JSONObject) : this(jsonObject.getInt("inSampleSize"))

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("inSampleSize", inSampleSize)
        }

    override fun toString(): String = key
}

fun List<Transformed>.getInSampledTransformed(): InSampledTransformed? =
    find { it is InSampledTransformed } as InSampledTransformed?