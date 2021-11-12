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

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.SketchUtils

/**
 * [Sketch] 明确支持的图片格式
 */
// 转成一个 support Types 列表，支持根据 mimeType get
enum class ImageType(
    var mimeType: String,
    var bestConfig: Bitmap.Config,
    lowQualityConfig: Bitmap.Config,
) {
    JPEG("image/jpeg", Bitmap.Config.ARGB_8888, Bitmap.Config.RGB_565), PNG(
        "image/png",
        Bitmap.Config.ARGB_8888,
        if (SketchUtils.isDisabledARGB4444) Bitmap.Config.ARGB_8888 else Bitmap.Config.ARGB_4444
    ),
    WEBP(
        "image/webp",
        Bitmap.Config.ARGB_8888,
        if (SketchUtils.isDisabledARGB4444) Bitmap.Config.ARGB_8888 else Bitmap.Config.ARGB_4444
    ),
    GIF(
        "image/gif",
        Bitmap.Config.ARGB_8888,
        if (SketchUtils.isDisabledARGB4444) Bitmap.Config.ARGB_8888 else Bitmap.Config.ARGB_4444
    ),
    BMP("image/bmp", Bitmap.Config.ARGB_8888, Bitmap.Config.RGB_565);

    var lowQualityConfig: Bitmap.Config = lowQualityConfig
        set(value) {
            field = if (value == Bitmap.Config.ARGB_4444 && SketchUtils.isDisabledARGB4444) {
                Bitmap.Config.ARGB_8888
            } else {
                lowQualityConfig
            }
        }

    fun getConfig(lowQualityImage: Boolean): Bitmap.Config {
        return if (lowQualityImage) lowQualityConfig else bestConfig
    }

    fun equals(mimeType: String?): Boolean {
        return this.mimeType.equals(mimeType, ignoreCase = true)
    }

    companion object {
        @JvmStatic
        fun valueOfMimeType(mimeType: String?): ImageType? {
            return when {
                JPEG.mimeType.equals(mimeType, ignoreCase = true) -> JPEG
                PNG.mimeType.equals(mimeType, ignoreCase = true) -> PNG
                WEBP.mimeType.equals(mimeType, ignoreCase = true) -> WEBP
                GIF.mimeType.equals(mimeType, ignoreCase = true) -> GIF
                BMP.mimeType.equals(mimeType, ignoreCase = true) -> BMP
                else -> null
            }
        }
    }
}