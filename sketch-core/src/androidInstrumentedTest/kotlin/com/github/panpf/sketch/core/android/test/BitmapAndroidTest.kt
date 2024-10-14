package com.github.panpf.sketch.core.android.test

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.os.Build
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.createEmptyBitmapWith
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.isImmutable
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.copyWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitmapAndroidTest {

    @Test
    fun testBitmapTypealias() {
        assertEquals(
            expected = android.graphics.Bitmap::class,
            actual = com.github.panpf.sketch.Bitmap::class
        )
    }

    @Test
    fun testWidth() {
        assertEquals(expected = 100, actual = createBitmap(100, 200).width)
        assertEquals(expected = 200, actual = createBitmap(200, 100).width)
    }

    @Test
    fun testHeight() {
        assertEquals(expected = 200, actual = createBitmap(100, 200).height)
        assertEquals(expected = 100, actual = createBitmap(200, 100).height)
    }

    @Test
    fun testSize() {
        assertEquals(expected = Size(100, 200), actual = createBitmap(100, 200).size)
        assertEquals(expected = Size(200, 100), actual = createBitmap(200, 100).size)
    }

    @Test
    fun testByteCount() {
        assertEquals(expected = 80000, actual = createBitmap(100, 200, ARGB_8888).byteCount)
        assertEquals(expected = 40000, actual = createBitmap(200, 100, RGB_565).byteCount)
    }

    @Test
    fun testIsMutable() {
        assertTrue(ResourceImages.jpeg.decode().bitmap.copyWith(isMutable = true).isMutable)
        assertFalse(ResourceImages.jpeg.decode().bitmap.isMutable)
    }

    @Test
    fun testIsImmutable() {
        assertFalse(ResourceImages.jpeg.decode().bitmap.copyWith(isMutable = true).isImmutable)
        assertTrue(ResourceImages.jpeg.decode().bitmap.isImmutable)
    }

    @Test
    fun testCreateBitmap() {
        createBitmap(width = 100, height = 200).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.ARGB_8888, actual = config)
            assertEquals(expected = true, actual = hasAlpha())
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }
        }
        createBitmap(width = 100, height = 200, config = ColorType.RGB_565).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = config)
            assertEquals(expected = false, actual = hasAlpha())
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createBitmap(
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

        createBitmap(100, 200).apply {
            assertEquals(
                expected = android.graphics.Bitmap.Config.ARGB_8888,
                actual = this.colorType
            )
        }
    }

    @Test
    fun testCreateEmptyBitmapWith() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Bitmap.createBitmap(
                /* width = */ 101,
                /* height = */ 202,
                /* config = */ Bitmap.Config.ARGB_8888,
                /* hasAlpha = */ true,
                /* colorSpace = */ ColorSpace.get(ColorSpace.Named.SRGB)
            ).apply {
                assertEquals(expected = 101, actual = width)
                assertEquals(expected = 202, actual = height)
                assertEquals(expected = ColorType.ARGB_8888, actual = config)
                assertEquals(expected = true, actual = hasAlpha())
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }.createEmptyBitmapWith(
                width = 202,
                height = 101,
                colorType = RGB_565,
                hasAlpha = false
            ).apply {
                assertEquals(expected = 202, actual = width)
                assertEquals(expected = 101, actual = height)
                assertEquals(expected = ColorType.RGB_565, actual = config)
                assertEquals(expected = false, actual = hasAlpha())
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }.createEmptyBitmapWith(
                size = Size(100, 100),
                colorType = RGB_565,
                hasAlpha = false
            ).apply {
                assertEquals(expected = 100, actual = width)
                assertEquals(expected = 100, actual = height)
                assertEquals(expected = ColorType.RGB_565, actual = config)
                assertEquals(expected = false, actual = hasAlpha())
                assertEquals(expected = ColorSpace.get(ColorSpace.Named.SRGB), actual = colorSpace)
            }

            Bitmap.createBitmap(
                /* width = */ 101,
                /* height = */ 202,
                /* config = */ Bitmap.Config.ARGB_8888,
                /* hasAlpha = */ true,
                /* colorSpace = */ ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
            ).apply {
                assertEquals(expected = 101, actual = width)
                assertEquals(expected = 202, actual = height)
                assertEquals(expected = ColorType.ARGB_8888, actual = config)
                assertEquals(expected = true, actual = hasAlpha())
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                    actual = colorSpace
                )
            }.createEmptyBitmapWith(
                width = 202,
                height = 101,
                colorType = RGB_565,
                hasAlpha = false
            ).apply {
                assertEquals(expected = 202, actual = width)
                assertEquals(expected = 101, actual = height)
                assertEquals(expected = ColorType.RGB_565, actual = config)
                assertEquals(expected = false, actual = hasAlpha())
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                    actual = colorSpace
                )
            }.createEmptyBitmapWith(
                size = Size(100, 100),
                colorType = RGB_565,
                hasAlpha = false
            ).apply {
                assertEquals(expected = 100, actual = width)
                assertEquals(expected = 100, actual = height)
                assertEquals(expected = ColorType.RGB_565, actual = config)
                assertEquals(expected = false, actual = hasAlpha())
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                    actual = colorSpace
                )
            }
        } else {
            Bitmap.createBitmap(
                /* width = */ 101,
                /* height = */ 202,
                /* config = */ Bitmap.Config.ARGB_8888,
            ).apply {
                assertEquals(expected = 101, actual = width)
                assertEquals(expected = 202, actual = height)
                assertEquals(expected = ColorType.ARGB_8888, actual = config)
            }.createEmptyBitmapWith(
                width = 202,
                height = 101,
                colorType = RGB_565,
                hasAlpha = false
            ).apply {
                assertEquals(expected = 202, actual = width)
                assertEquals(expected = 101, actual = height)
                assertEquals(expected = ColorType.RGB_565, actual = config)
            }.createEmptyBitmapWith(
                size = Size(100, 100),
                colorType = RGB_565,
                hasAlpha = false
            ).apply {
                assertEquals(expected = 100, actual = width)
                assertEquals(expected = 100, actual = height)
                assertEquals(expected = ColorType.RGB_565, actual = config)
            }
        }
    }
}