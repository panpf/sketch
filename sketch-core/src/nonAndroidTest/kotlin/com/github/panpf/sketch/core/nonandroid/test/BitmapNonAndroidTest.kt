package com.github.panpf.sketch.core.nonandroid.test

import kotlin.test.Test
import kotlin.test.assertEquals

class BitmapNonAndroidTest {

    @Test
    fun testBitmap() {
        assertEquals(
            expected = org.jetbrains.skia.Bitmap::class,
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

    @Test
    fun testAllocationByteCount() {
        // TODO test
    }
}