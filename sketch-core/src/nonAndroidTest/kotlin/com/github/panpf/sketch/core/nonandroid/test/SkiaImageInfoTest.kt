package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaImageInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class SkiaImageInfoTest {

    @Test
    fun testSkiaImageInfo() {
        assertEquals(
            expected = org.jetbrains.skia.ImageInfo::class,
            actual = SkiaImageInfo::class
        )
    }
}