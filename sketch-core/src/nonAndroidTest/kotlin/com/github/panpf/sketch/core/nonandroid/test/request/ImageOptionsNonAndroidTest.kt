package com.github.panpf.sketch.core.nonandroid.test.request

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
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
}