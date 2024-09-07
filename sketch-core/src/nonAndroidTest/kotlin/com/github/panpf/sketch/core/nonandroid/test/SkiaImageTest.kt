package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaImage
import kotlin.test.Test
import kotlin.test.assertEquals

class SkiaImageTest {

    @Test
    fun testSkiaImage() {
        assertEquals(
            expected = org.jetbrains.skia.Image::class,
            actual = SkiaImage::class
        )
    }
}