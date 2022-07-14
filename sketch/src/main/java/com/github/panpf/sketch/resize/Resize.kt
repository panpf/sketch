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
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.format
import org.json.JSONObject

data class Resize constructor(
    val width: Int,
    val height: Int,
    val precision: PrecisionDecider,
    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scale: ScaleDecider,
) : JsonSerializable {

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.EXACTLY,
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
    ) : this(width, height, fixedPrecision(Precision.EXACTLY), fixedScale(scale))

    constructor(
        width: Int,
        height: Int,
        precision: PrecisionDecider,
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, precision, fixedScale(scale))

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.EXACTLY,
        scale: ScaleDecider
    ) : this(width, height, fixedPrecision(precision), scale)


    constructor(
        size: Size,
        precision: PrecisionDecider,
        scale: ScaleDecider
    ) : this(size.width, size.height, precision, scale)

    constructor(
        size: Size,
        precision: Precision = Precision.EXACTLY,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, fixedPrecision(precision), fixedScale(scale))

    constructor(
        size: Size,
        precision: Precision,
    ) : this(size.width, size.height, fixedPrecision(precision), fixedScale(Scale.CENTER_CROP))

    constructor(
        size: Size,
        scale: Scale
    ) : this(size.width, size.height, fixedPrecision(Precision.EXACTLY), fixedScale(scale))

    constructor(
        size: Size,
        precision: PrecisionDecider,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, precision, fixedScale(scale))

    constructor(
        size: Size,
        precision: Precision = Precision.EXACTLY,
        scale: ScaleDecider
    ) : this(size.width, size.height, fixedPrecision(precision), scale)

    val key: String by lazy {
        "Resize(${width}x$height,${precision.key},${scale.key})"
    }

    fun getPrecision(imageWidth: Int, imageHeight: Int): Precision =
        precision.get(imageWidth, imageHeight, width, height)

    fun getScale(imageWidth: Int, imageHeight: Int): Scale =
        scale.get(imageWidth, imageHeight, width, height)

    fun shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        when (getPrecision(imageWidth, imageHeight)) {
            Precision.EXACTLY -> imageWidth != width || imageHeight != height
            Precision.SAME_ASPECT_RATIO -> {
                val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
                val resizeAspectRatio = width.toFloat().div(height).format(1)
                imageAspectRatio != resizeAspectRatio
            }
            Precision.LESS_PIXELS -> false
        }

    override fun toString(): String {
        return "Resize(width=${width}, height=$height, precision=${precision}, scale=${scale})"
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<Resize> {
        override fun toJson(t: Resize): JSONObject =
            JSONObject().apply {
                put("width", t.width)
                put("height", t.height)

                val precisionSerializerClass =
                    t.precision.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                val precisionSerializer = precisionSerializerClass.newInstance()
                put("precisionDeciderSerializerClassName", precisionSerializerClass.name)
                put("precisionDeciderContent", precisionSerializer.toJson(t.precision))

                val scaleDeciderSerializerClass =
                    t.scale.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                val scaleDeciderSerializer = scaleDeciderSerializerClass.newInstance()
                put("scaleDeciderSerializerClassName", scaleDeciderSerializerClass.name)
                put("scaleDeciderContent", scaleDeciderSerializer.toJson(t.scale))
            }

        override fun fromJson(jsonObject: JSONObject): Resize =
            Resize(
                width = jsonObject.getInt("width"),
                height = jsonObject.getInt("height"),
                precision = jsonObject.getString("precisionDeciderSerializerClassName")
                    .let { Class.forName(it) }
                    .newInstance().asOrThrow<JsonSerializer<*>>()
                    .fromJson(jsonObject.getJSONObject("precisionDeciderContent"))!!
                    .asOrThrow<PrecisionDecider>(),
                scale = jsonObject.getString("scaleDeciderSerializerClassName")
                    .let { Class.forName(it) }
                    .newInstance().asOrThrow<JsonSerializer<*>>()
                    .fromJson(jsonObject.getJSONObject("scaleDeciderContent"))!!
                    .asOrThrow<ScaleDecider>(),
            )
    }
}