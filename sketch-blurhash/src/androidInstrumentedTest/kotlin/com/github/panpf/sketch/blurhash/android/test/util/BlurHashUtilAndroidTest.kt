package com.github.panpf.sketch.blurhash.android.test.util

import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.util.createBlurHashBitmap
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurHashUtilAndroidTest {

    @Test
    fun testCreateBlurHashBitmap() {
        createBlurHashBitmap(101, 202).apply {
            assertEquals(expected = 101, actual = width)
            assertEquals(expected = 202, actual = height)
            assertEquals(expected = ColorType.ARGB_8888, actual = colorType)
        }
    }
}