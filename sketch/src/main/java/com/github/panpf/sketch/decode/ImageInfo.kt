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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.decode.internal.ExifOrientationCorrector
import org.json.JSONException
import org.json.JSONObject

data class ImageInfo constructor(
    val mimeType: String,
    val width: Int,
    val height: Int,
    val exifOrientation: Int
) {

    fun toJsonString(): String = JSONObject().apply {
        put("mimeType", mimeType)
        put("width", width)
        put("height", height)
        put("exifOrientation", exifOrientation)
    }.toString()

    override fun toString(): String {
        val exifOrientationName = ExifOrientationCorrector.toName(exifOrientation)
        return "ImageInfo(mimeType='$mimeType',width=$width,height=$height,exifOrientation=${exifOrientationName})"
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJsonString(jsonString: String): ImageInfo {
            val json = JSONObject(jsonString)
            return ImageInfo(
                json.getString("mimeType"),
                json.getInt("width"),
                json.getInt("height"),
                json.getInt("exifOrientation"),
            )
        }
    }
}