package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import org.json.JSONObject

/**
 * Decide which precision to use
 */
@Keep
interface PrecisionDecider {
    fun precision(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision
    fun serializationToJSON(): JSONObject
}