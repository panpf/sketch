package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.JsonSerializable

/**
 * Decide which precision to use
 */
interface PrecisionDecider : JsonSerializable {

    val key: String

    fun get(
        sketch: Sketch, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision
}