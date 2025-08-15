package com.github.panpf.sketch.blurhash.nonandroid.test.util

import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.util.createBlurHashBitmap
import com.github.panpf.sketch.util.decodeBlurHashToBitmap
import org.jetbrains.skia.ColorSpace
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurhashsNonAndroidTest {

    @Test
    fun testCreateBlurHashBitmap() {
        createBlurHashBitmap(101, 202).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }

        val decodeConfig =
            DecodeConfig(colorType = ColorType.RGB_565, colorSpace = ColorSpace.displayP3)
        createBlurHashBitmap(101, 202, decodeConfig).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorSpace.displayP3, actual = colorSpace)
        }
    }

    @Test
    fun testDecodeBlurHashToBitmap() {
        val blurHash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj"

        decodeBlurHashToBitmap(blurHash, 101, 202).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }

        val decodeConfig =
            DecodeConfig(colorType = ColorType.RGB_565, colorSpace = ColorSpace.displayP3)
        decodeBlurHashToBitmap(blurHash, 101, 202, decodeConfig = decodeConfig).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorSpace.displayP3, actual = colorSpace)
        }
    }
}