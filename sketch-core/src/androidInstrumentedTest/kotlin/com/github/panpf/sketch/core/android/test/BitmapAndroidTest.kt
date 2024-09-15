package com.github.panpf.sketch.core.android.test

import kotlin.test.Test
import kotlin.test.assertEquals

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
        // TODO test
    }

    @Test
    fun testHeight() {
        // TODO test
    }

    @Test
    fun testByteCount() {
        // TODO test
    }
}