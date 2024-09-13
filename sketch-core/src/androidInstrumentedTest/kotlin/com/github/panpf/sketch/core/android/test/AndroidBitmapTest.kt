package com.github.panpf.sketch.core.android.test

import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidBitmapTest {

    @Test
    fun testAndroidBitmap() {
        assertEquals(
            expected = android.graphics.Bitmap::class,
            actual = AndroidBitmap::class
        )

        AndroidBitmap(100, 200).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.ARGB_8888, actual = config)
        }
        AndroidBitmap(100, 200, ColorType.RGB_565).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = config)
        }
    }

    @Test
    fun testColorType() {
        assertEquals(
            expected = android.graphics.Bitmap.Config::class,
            actual = ColorType::class
        )
    }
}