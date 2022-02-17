/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import org.json.JSONObject

@Keep
data class Resize constructor(
    val width: Int,
    val height: Int,
    val precisionDecider: PrecisionDecider,
    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.KEEP_ASPECT_RATIO]
     */
    val scale: Scale = Scale.CENTER_CROP,
) {

    constructor(
        size: Size,
        precision: Precision = Precision.LESS_PIXELS,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, fixedPrecision(precision), scale)

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.LESS_PIXELS,
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, fixedPrecision(precision), scale)

    constructor(
        width: Int,
        height: Int,
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, fixedPrecision(Precision.LESS_PIXELS), scale)

    constructor(
        size: Size,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, fixedPrecision(Precision.LESS_PIXELS), scale)

    val cacheKey: String by lazy {
        val precisionDeciderString = precisionDecider.toString().replace("PrecisionDecider", "")
        "Resize(${width}x$height,${precisionDeciderString},${scale})"
    }

    fun precision(imageWidth: Int, imageHeight: Int): Precision =
        precisionDecider.precision(imageWidth, imageHeight, width, height)

    fun shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        when (precision(imageWidth, imageHeight)) {
            Precision.KEEP_ASPECT_RATIO -> {
                val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
                val resizeAspectRatio = width.toFloat().div(height).format(1)
                imageAspectRatio != resizeAspectRatio
            }
            Precision.EXACTLY -> imageWidth != width || imageHeight != height
            Precision.LESS_PIXELS -> false
        }

    @Keep
    constructor(jsonObject: JSONObject) : this(
        width = jsonObject.getInt("width"),
        height = jsonObject.getInt("height"),
        precisionDecider = Class.forName(jsonObject.getString("precisionDeciderClassName"))
            .getConstructor(JSONObject::class.java)
            .newInstance(jsonObject.getJSONObject("precisionDeciderContent")) as PrecisionDecider,
        scale = Scale.valueOf(jsonObject.getString("scale"))
    )

    fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("width", width)
            put("height", height)
            put("precisionDeciderClassName", precisionDecider::class.java.name)
            put("precisionDeciderContent", precisionDecider.serializationToJSON())
            put("scale", scale.name)
        }

    override fun toString(): String = cacheKey
}