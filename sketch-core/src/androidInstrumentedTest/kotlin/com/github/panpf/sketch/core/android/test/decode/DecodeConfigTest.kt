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
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.SMPTE_C
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.toBitmapOptions
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class DecodeConfigTest {

    @Test
    fun testDecodeConfig() {
        // TODO test
    }

    @Test
    fun testInSampleSize() {
        DecodeConfig().apply {
            assertNull(sampleSize)

            sampleSize = 4
            assertEquals(4, sampleSize)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testInPreferQualityOverSpeed() {
        DecodeConfig().apply {
            assertNull(inPreferQualityOverSpeed)

            inPreferQualityOverSpeed = false
            assertEquals(false, inPreferQualityOverSpeed)
        }
    }

    @Test
    fun testInPreferredConfig() {
        DecodeConfig().apply {
            assertNull(colorType)

            colorType = RGB_565
            assertEquals(RGB_565, colorType)
        }
    }

    @Test
    fun testInPreferredColorSpace() {
        DecodeConfig().apply {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertNull(colorSpace)
            }

            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                colorSpace = ColorSpace.get(SMPTE_C)
                assertEquals(ColorSpace.get(SMPTE_C), colorSpace)
            }
        }
    }

    @Test
    fun testToBitmapOptions() {
        DecodeConfig().toBitmapOptions().apply {
            assertEquals(0, inSampleSize)
            @Suppress("DEPRECATION")
            assertEquals(false, inPreferQualityOverSpeed)
            assertEquals(Bitmap.Config.ARGB_8888, inPreferredConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(null, inPreferredColorSpace)
            }
        }

        DecodeConfig().apply {
            sampleSize = 4
            @Suppress("DEPRECATION")
            inPreferQualityOverSpeed = true
            colorType = RGB_565
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                colorSpace = ColorSpace.get(SMPTE_C)
            }
        }.toBitmapOptions().apply {
            assertEquals(4, inSampleSize)
            @Suppress("DEPRECATION")
            if (VERSION.SDK_INT <= VERSION_CODES.M) {
                assertEquals(true, inPreferQualityOverSpeed)
            } else {
                assertEquals(false, inPreferQualityOverSpeed)
            }
            assertEquals(RGB_565, inPreferredConfig)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(ColorSpace.get(SMPTE_C), inPreferredColorSpace)
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        // TODO test
    }

    @Test
    fun testToString() {
        // TODO test
    }
}