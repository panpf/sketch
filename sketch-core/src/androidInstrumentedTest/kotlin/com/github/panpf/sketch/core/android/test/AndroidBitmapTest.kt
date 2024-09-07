package com.github.panpf.sketch.core.android.test

import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.AndroidBitmapConfig
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
            assertEquals(expected = AndroidBitmapConfig.ARGB_8888, actual = config)
        }
        AndroidBitmap(100, 200, AndroidBitmapConfig.RGB_565).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = AndroidBitmapConfig.RGB_565, actual = config)
        }
    }

    @Test
    fun testAndroidBitmapConfig() {
        assertEquals(
            expected = android.graphics.Bitmap.Config::class,
            actual = AndroidBitmapConfig::class
        )
    }
}