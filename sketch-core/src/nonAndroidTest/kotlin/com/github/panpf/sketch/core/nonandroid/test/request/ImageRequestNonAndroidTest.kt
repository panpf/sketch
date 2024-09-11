package com.github.panpf.sketch.core.nonandroid.test.request

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.test.utils.getTestContext
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestNonAndroidTest {

    @Test
    fun testBitmapConfig() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            bitmapConfig(ColorType.RGB_565)
        }.apply {
            assertEquals(BitmapConfig.FixedQuality(ColorType.RGB_565.name), bitmapConfig)
        }
    }

    @Test
    fun testColorSpace() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            colorSpace(ColorSpace.displayP3)
        }.apply {
            assertEquals("displayP3", colorSpace)
        }
    }
}