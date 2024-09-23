package com.github.panpf.sketch.core.android.test

import android.graphics.ColorSpace
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidBitmapTest {

    @Test
    fun testAndroidBitmap() {
        assertEquals(
            expected = android.graphics.Bitmap::class,
            actual = AndroidBitmap::class
        )

        AndroidBitmap(width = 100, height = 200).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.ARGB_8888, actual = config)
            assertEquals(expected = true, actual = hasAlpha())
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }
        }
        AndroidBitmap(width = 100, height = 200, config = ColorType.RGB_565).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = config)
            assertEquals(expected = false, actual = hasAlpha())
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AndroidBitmap(
                width = 100,
                height = 200,
                config = ColorType.ARGB_8888,
                hasAlpha = false,
                colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
            ).apply {
                assertEquals(expected = 100, actual = width)
                assertEquals(expected = 200, actual = height)
                assertEquals(expected = ColorType.ARGB_8888, actual = config)
                assertEquals(expected = false, actual = hasAlpha())
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                        actual = colorSpace
                    )
                }
            }
        }
    }

    @Test
    fun testColorType() {
        assertEquals(
            expected = android.graphics.Bitmap.Config::class,
            actual = ColorType::class
        )

        AndroidBitmap(100, 200).apply {
            assertEquals(
                expected = android.graphics.Bitmap.Config.ARGB_8888,
                actual = this.colorType
            )
        }
    }
}