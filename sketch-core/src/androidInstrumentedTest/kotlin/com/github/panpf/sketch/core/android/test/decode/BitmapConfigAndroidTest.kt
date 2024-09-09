@file:Suppress("DEPRECATION")

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

package com.github.panpf.sketch.core.android.test.decode

import android.graphics.Bitmap
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.BitmapConfig.FixedQuality
import com.github.panpf.sketch.decode.toAndroidBitmapConfig
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class BitmapConfigAndroidTest {

    @Test
    fun testBitmapConfig() {
        assertEquals(
            expected = FixedQuality(Bitmap.Config.ARGB_8888.name),
            actual = BitmapConfig(Bitmap.Config.ARGB_8888)
        )
        assertEquals(
            expected = FixedQuality(Bitmap.Config.RGB_565.name),
            actual = BitmapConfig(Bitmap.Config.RGB_565)
        )
    }

    @Test
    fun testToAndroidBitmapConfig() {
        BitmapConfig.HighQuality.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(Bitmap.Config.RGBA_F16, toAndroidBitmapConfig("image/jpeg"))
                assertEquals(Bitmap.Config.RGBA_F16, toAndroidBitmapConfig("image/png"))
                assertEquals(Bitmap.Config.RGBA_F16, toAndroidBitmapConfig(null))
            } else {
                assertEquals(Bitmap.Config.ARGB_8888, toAndroidBitmapConfig("image/jpeg"))
                assertEquals(Bitmap.Config.ARGB_8888, toAndroidBitmapConfig("image/png"))
                assertEquals(Bitmap.Config.ARGB_8888, toAndroidBitmapConfig(null))
            }
        }

        BitmapConfig.LowQuality.apply {
            assertEquals(Bitmap.Config.RGB_565, toAndroidBitmapConfig("image/jpeg"))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                (assertEquals(Bitmap.Config.ARGB_4444, toAndroidBitmapConfig("image/png")))
            } else {
                assertEquals(Bitmap.Config.ARGB_8888, toAndroidBitmapConfig("image/png"))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                (assertEquals(Bitmap.Config.ARGB_4444, toAndroidBitmapConfig(null)))
            } else {
                assertEquals(Bitmap.Config.ARGB_8888, toAndroidBitmapConfig(null))
            }
        }

        BitmapConfig(Bitmap.Config.RGB_565).apply {
            assertEquals(Bitmap.Config.RGB_565, toAndroidBitmapConfig("image/jpeg"))
            assertEquals(Bitmap.Config.RGB_565, toAndroidBitmapConfig("image/png"))
            assertEquals(Bitmap.Config.RGB_565, toAndroidBitmapConfig(null))
        }
    }
}