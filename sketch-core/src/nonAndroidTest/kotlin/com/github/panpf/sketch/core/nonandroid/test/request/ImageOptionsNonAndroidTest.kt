package com.github.panpf.sketch.core.nonandroid.test.request

import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageOptionsNonAndroidTest {

    @Test
    fun testColorType() {
        ImageOptions {
            colorType(ColorType.RGB_565)
        }.apply {
            assertEquals(BitmapColorType(ColorType.RGB_565.name), colorType)
        }
    }

    @Test
    fun testColorSpace() {
        ImageOptions {
            colorSpace(ColorSpace.displayP3)
        }.apply {
            assertEquals(BitmapColorSpace("displayP3"), colorSpace)
        }
    }
}