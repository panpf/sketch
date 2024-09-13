package com.github.panpf.sketch.core.nonandroid.test.request

import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.test.utils.getTestContext
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestNonAndroidTest {

    @Test
    fun testColorType() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            colorType(ColorType.RGB_565)
        }.apply {
            assertEquals(BitmapColorType(ColorType.RGB_565.name), colorType)
        }
    }

    @Test
    fun testColorSpace() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            colorSpace(ColorSpace.displayP3)
        }.apply {
            assertEquals(BitmapColorSpace("displayP3"), colorSpace)
        }
    }
}