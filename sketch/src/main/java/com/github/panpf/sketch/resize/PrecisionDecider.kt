package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import org.json.JSONObject

/**
 * Decide which precision to use
 */
@Keep
interface PrecisionDecider {

    val key: String

    fun get(
        sketch: Sketch, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision

    fun serializationToJSON(): JSONObject
}