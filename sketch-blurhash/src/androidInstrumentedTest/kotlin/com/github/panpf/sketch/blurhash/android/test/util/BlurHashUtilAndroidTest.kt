package com.github.panpf.sketch.blurhash.android.test.util

import android.graphics.ColorSpace
import android.os.Build
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.util.createBlurHashBitmap
import com.github.panpf.sketch.util.decodeBlurHashToBitmap
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurHashUtilAndroidTest {

    @Test
    fun testCreateBlurHashBitmap() {
        createBlurHashBitmap(101, 202).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.ARGB_8888, actual = colorType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }
        }

        val decodeConfig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DecodeConfig(
                colorType = ColorType.RGB_565,
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
            )
        } else {
            DecodeConfig(colorType = ColorType.RGB_565)
        }
        createBlurHashBitmap(101, 202, decodeConfig).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                    actual = colorSpace
                )
            }
        }
    }

    @Test
    fun testDecodeBlurHashToBitmap() {
        val blurHash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj"

        decodeBlurHashToBitmap(blurHash, 101, 202).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.ARGB_8888, actual = colorType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }
        }

        val decodeConfig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DecodeConfig(
                colorType = ColorType.RGB_565,
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
            )
        } else {
            DecodeConfig(colorType = ColorType.RGB_565)
        }
        decodeBlurHashToBitmap(blurHash, 101, 202, decodeConfig = decodeConfig).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                    actual = colorSpace
                )
            }
        }
    }
}