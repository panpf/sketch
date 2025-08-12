package com.github.panpf.sketch.blurhash.nonandroid.test.util

import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.util.createBlurHashBitmap
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurHashUtilNonAndroidTest {

    @Test
    fun testCreateBlurHashBitmap() {
        createBlurHashBitmap(101, 202).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
        }
    }
}