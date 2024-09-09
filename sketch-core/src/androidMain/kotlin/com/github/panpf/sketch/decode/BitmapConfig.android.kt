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

import android.graphics.Bitmap
import android.os.Build
import com.github.panpf.sketch.AndroidBitmapConfig
import com.github.panpf.sketch.decode.BitmapConfig.FixedQuality
import com.github.panpf.sketch.decode.internal.ImageFormat

/**
 * Build a [BitmapConfig] with the specified [config]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapConfigAndroidTest.testBitmapConfig
 */
fun BitmapConfig(config: Bitmap.Config): BitmapConfig = FixedQuality(config.name)

/**
 * Convert [BitmapConfig] to [Bitmap.Config]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapConfigAndroidTest.testToAndroidBitmapConfig
 */
fun BitmapConfig.toAndroidBitmapConfig(mimeType: String?): AndroidBitmapConfig = when (this) {
    is BitmapConfig.HighQuality -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Bitmap.Config.RGBA_F16
        } else {
            Bitmap.Config.ARGB_8888
        }
    }

    is BitmapConfig.LowQuality -> {
        if (ImageFormat.parseMimeType(mimeType) == ImageFormat.JPEG) {
            Bitmap.Config.RGB_565
        } else {
            Bitmap.Config.ARGB_8888
        }
    }

    is FixedQuality -> Bitmap.Config.valueOf(this.value)
}