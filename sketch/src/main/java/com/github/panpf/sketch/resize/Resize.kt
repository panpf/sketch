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
    val precision: PrecisionDecider,
    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.KEEP_ASPECT_RATIO]
     */
    val scale: ScaleDecider,
) {

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.LESS_PIXELS,
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, fixedPrecision(precision), fixedScale(scale))

    constructor(
        width: Int,
        height: Int,
        precision: Precision,
    ) : this(width, height, fixedPrecision(precision), fixedScale(Scale.CENTER_CROP))

    constructor(
        width: Int,
        height: Int,
        scale: Scale
    ) : this(width, height, fixedPrecision(Precision.LESS_PIXELS), fixedScale(scale))

    constructor(
        width: Int,
        height: Int,
        precision: PrecisionDecider,
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, precision, fixedScale(scale))

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.LESS_PIXELS,
        scale: ScaleDecider
    ) : this(width, height, fixedPrecision(precision), scale)

    constructor(
        width: Int,
        height: Int,
        scale: ScaleDecider
    ) : this(width, height, fixedPrecision(Precision.LESS_PIXELS), scale)


    constructor(
        size: Size,
        precision: Precision = Precision.LESS_PIXELS,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, fixedPrecision(precision), fixedScale(scale))

    constructor(
        size: Size,
        precision: Precision,
    ) : this(size.width, size.height, fixedPrecision(precision), fixedScale(Scale.CENTER_CROP))

    constructor(
        size: Size,
        scale: Scale
    ) : this(size.width, size.height, fixedPrecision(Precision.LESS_PIXELS), fixedScale(scale))

    constructor(
        size: Size,
        precision: PrecisionDecider,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, precision, fixedScale(scale))

    constructor(
        size: Size,
        precision: Precision = Precision.LESS_PIXELS,
        scale: ScaleDecider
    ) : this(size.width, size.height, fixedPrecision(precision), scale)

    constructor(
        size: Size,
        scale: ScaleDecider
    ) : this(size.width, size.height, fixedPrecision(Precision.LESS_PIXELS), scale)

    val key: String by lazy { toString() }

    fun getPrecision(imageWidth: Int, imageHeight: Int): Precision =
        precision.get(imageWidth, imageHeight, width, height)

    fun getScale(imageWidth: Int, imageHeight: Int): Scale =
        scale.get(imageWidth, imageHeight, width, height)

    fun shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        when (getPrecision(imageWidth, imageHeight)) {
            Precision.KEEP_ASPECT_RATIO -> {
                val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
                val resizeAspectRatio = width.toFloat().div(height).format(1)
                imageAspectRatio != resizeAspectRatio
            }
            Precision.EXACTLY -> imageWidth != width || imageHeight != height
            Precision.LESS_PIXELS -> imageWidth * imageHeight > width * height
        }

    @Keep
    constructor(jsonObject: JSONObject) : this(
        width = jsonObject.getInt("width"),
        height = jsonObject.getInt("height"),
        precision = Class.forName(jsonObject.getString("precisionDeciderClassName"))
            .getConstructor(JSONObject::class.java)
            .newInstance(jsonObject.getJSONObject("precisionDeciderContent")) as PrecisionDecider,
        scale = Class.forName(jsonObject.getString("scaleDeciderClassName"))
            .getConstructor(JSONObject::class.java)
            .newInstance(jsonObject.getJSONObject("scaleDeciderContent")) as ScaleDecider,
    )

    fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("width", width)
            put("height", height)
            put("precisionDeciderClassName", precision::class.java.name)
            put("precisionDeciderContent", precision.serializationToJSON())
            put("scaleDeciderClassName", scale::class.java.name)
            put("scaleDeciderContent", scale.serializationToJSON())
        }

    override fun toString(): String {
        val precisionDeciderString = precision.key.replace("PrecisionDecider", "")
        val scaleDeciderString = scale.key.replace("ScaleDecider", "")
        return "Resize(${width}x$height,${precisionDeciderString},${scaleDeciderString})"
    }
}