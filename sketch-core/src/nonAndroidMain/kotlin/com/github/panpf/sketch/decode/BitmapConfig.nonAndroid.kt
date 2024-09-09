/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.decode.BitmapConfig.FixedQuality
import com.github.panpf.sketch.decode.internal.ImageFormat
import org.jetbrains.skia.ColorType

/**
 * Build a [ColorType] with the specified [config]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapConfigNonAndroidTest.testBitmapConfig
 */
fun BitmapConfig(config: ColorType): BitmapConfig = FixedQuality(config.name)

/**
 * Convert [BitmapConfig] to [ColorType]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapConfigNonAndroidTest.testToSkiaBitmapConfig
 */
fun BitmapConfig.toSkiaColorType(mimeType: String?): ColorType = when (this) {
    is BitmapConfig.HighQuality -> {
        ColorType.RGBA_F16
    }

    is BitmapConfig.LowQuality -> {
        if (ImageFormat.parseMimeType(mimeType) == ImageFormat.JPEG) {
            ColorType.RGB_565
        } else {
            ColorType.RGBA_8888
        }
    }

    is FixedQuality -> ColorType.valueOf(this.value)
}