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

@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.core.android.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.SMPTE_C
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.decode.toBitmapOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class DecodeConfigTest {

    @Test
    fun testDecodeConfig() {
        val context = getTestContext()

        assertEquals(
            expected = DecodeConfig(),
            actual = DecodeConfig(ImageRequest(context, "test.jpg"))
        )

        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                ImageRequest(context, "test.jpg") {
                    colorType(ColorType.RGB_565)
                }
            )
        )

        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/jpeg",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = null),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/webp",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = null),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/gif",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = null),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = false
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = true
            )
        )

        val expectedHighQualityColorType = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ColorType.RGBA_F16
        } else {
            null
        }
        assertEquals(
            expected = DecodeConfig(colorType = expectedHighQualityColorType),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/jpeg",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = expectedHighQualityColorType),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/webp",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = expectedHighQualityColorType),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/gif",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = expectedHighQualityColorType),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = false
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = expectedHighQualityColorType),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = true
            )
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertEquals(
                expected = DecodeConfig(colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)),
                actual = DecodeConfig(
                    ImageRequest(context, "test.jpg") {
                        colorSpace(ColorSpace.Named.DISPLAY_P3)
                    }
                )
            )
        }

        assertEquals(
            expected = DecodeConfig(preferQualityOverSpeed = true),
            actual = DecodeConfig(
                ImageRequest(context, "test.jpg") {
                    preferQualityOverSpeed(true)
                }
            )
        )
    }

    @Test
    fun testConstructor() {
        DecodeConfig().apply {
            assertEquals(null, sampleSize)
            assertEquals(null, colorType)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(null, colorSpace)
            }
            assertEquals(null, preferQualityOverSpeed)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.RGB_565,
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                preferQualityOverSpeed = true
            ).apply {
                assertEquals(2, sampleSize)
                assertEquals(ColorType.RGB_565, colorType)
                assertEquals(ColorSpace.get(ColorSpace.Named.DISPLAY_P3), colorSpace)
                assertEquals(true, preferQualityOverSpeed)
            }
        } else {
            DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.RGB_565,
                preferQualityOverSpeed = true
            ).apply {
                assertEquals(2, sampleSize)
                assertEquals(ColorType.RGB_565, colorType)
                assertEquals(true, preferQualityOverSpeed)
            }
        }
    }

    @Test
    fun testFields() {
        DecodeConfig().apply {
            assertEquals(null, sampleSize)
            assertEquals(null, colorType)
            assertEquals(null, colorSpace)
            assertEquals(null, preferQualityOverSpeed)

            sampleSize = 2
            assertEquals(2, sampleSize)
            assertEquals(null, colorType)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(null, colorSpace)
            }
            assertEquals(null, preferQualityOverSpeed)

            colorType = ColorType.RGB_565
            assertEquals(2, sampleSize)
            assertEquals(ColorType.RGB_565, colorType)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(null, colorSpace)
            }
            assertEquals(null, preferQualityOverSpeed)

            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
                assertEquals(2, sampleSize)
                assertEquals(ColorType.RGB_565, colorType)
                assertEquals(ColorSpace.get(ColorSpace.Named.DISPLAY_P3), colorSpace)
                assertEquals(null, preferQualityOverSpeed)
            }

            preferQualityOverSpeed = true
            assertEquals(2, sampleSize)
            assertEquals(ColorType.RGB_565, colorType)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(ColorSpace.get(ColorSpace.Named.DISPLAY_P3), colorSpace)
            }
            assertEquals(true, preferQualityOverSpeed)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DecodeConfig()
        val element11 = DecodeConfig()
        val element2 = DecodeConfig(
            sampleSize = 2,
        )
        val element3 = DecodeConfig(
            sampleSize = 2,
            colorType = ColorType.RGB_565,
        )
        val element4 = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.RGB_565,
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
            )
        } else {
            DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.ARGB_4444,
            )
        }
        val element5 = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.RGB_565,
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                preferQualityOverSpeed = true
            )
        } else {
            DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.ARGB_4444,
                preferQualityOverSpeed = true
            )
        }

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertEquals(
                expected = "DecodeConfig(sampleSize=null, colorType=null, colorSpace=null, preferQualityOverSpeed=null)",
                actual = DecodeConfig().toString()
            )
            assertEquals(
                expected = "DecodeConfig(sampleSize=2, colorType=RGB_565, colorSpace=SRGB, preferQualityOverSpeed=true)",
                actual = DecodeConfig(
                    sampleSize = 2,
                    colorType = ColorType.RGB_565,
                    colorSpace = ColorSpace.get(ColorSpace.Named.SRGB),
                    preferQualityOverSpeed = true
                ).toString()
            )
        } else {
            assertEquals(
                expected = "DecodeConfig(sampleSize=null, colorType=null, preferQualityOverSpeed=null)",
                actual = DecodeConfig().toString()
            )
            assertEquals(
                expected = "DecodeConfig(sampleSize=2, colorType=RGB_565, preferQualityOverSpeed=true)",
                actual = DecodeConfig(
                    sampleSize = 2,
                    colorType = ColorType.RGB_565,
                    preferQualityOverSpeed = true
                ).toString()
            )
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
            preferQualityOverSpeed = true
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
}