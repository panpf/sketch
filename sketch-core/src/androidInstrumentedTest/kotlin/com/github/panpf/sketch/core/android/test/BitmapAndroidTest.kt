package com.github.panpf.sketch.core.android.test

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.isImmutable
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.util.copyWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitmapAndroidTest {

    @Test
    fun testBitmap() {
        assertEquals(
            expected = android.graphics.Bitmap::class,
            actual = com.github.panpf.sketch.Bitmap::class
        )
    }

    @Test
    fun testWidth() {
        assertEquals(expected = 100, actual = AndroidBitmap(100, 200).width)
        assertEquals(expected = 200, actual = AndroidBitmap(200, 100).width)
    }

    @Test
    fun testHeight() {
        assertEquals(expected = 200, actual = AndroidBitmap(100, 200).height)
        assertEquals(expected = 100, actual = AndroidBitmap(200, 100).height)
    }

    @Test
    fun testByteCount() {
        assertEquals(expected = 80000, actual = AndroidBitmap(100, 200, ARGB_8888).byteCount)
        assertEquals(expected = 40000, actual = AndroidBitmap(200, 100, RGB_565).byteCount)
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
}