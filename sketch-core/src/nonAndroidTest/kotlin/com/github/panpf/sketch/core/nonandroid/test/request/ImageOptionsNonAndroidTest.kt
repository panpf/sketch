package com.github.panpf.sketch.core.nonandroid.test.request

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageOptionsNonAndroidTest {

    @Test
    fun testBitmapConfig() {
        ImageOptions {
            bitmapConfig(ColorType.RGB_565)
        }.apply {
            assertEquals(BitmapConfig.FixedQuality(ColorType.RGB_565.name), bitmapConfig)
        }
    }

    @Test
    fun testColorSpace() {
        ImageOptions {
            colorSpace(ColorSpace.displayP3)
        }.apply {
            assertEquals("displayP3", colorSpace)
        }
    }
}