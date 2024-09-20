package com.github.panpf.sketch.core.nonandroid.test.decode

import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.test.utils.getTestContext
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DecodeConfigTest {

    @Test
    fun testDecodeConfig() {
        val context = getTestContext()

        assertEquals(
            expected = DecodeConfig(),
            actual = DecodeConfig(ImageRequest(context, "test.jpg"))
        )

        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                ImageRequest(context, "test.jpg") {
                    colorType(ColorType.RGB_565)
                }
            )
        )

        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/jpeg",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/webp",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = null),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/gif",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.ARGB_4444),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = false
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGB_565),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(LowQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = true
            )
        )

        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGBA_F16),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/jpeg",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGBA_F16),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/webp",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGBA_F16),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/gif",
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGBA_F16),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = false
            )
        )
        assertEquals(
            expected = DecodeConfig(colorType = ColorType.RGBA_F16),
            actual = DecodeConfig(
                request = ImageRequest(context, "test.jpg") {
                    colorType(HighQualityColorType)
                },
                mimeType = "image/png",
                isOpaque = true
            )
        )

        assertEquals(
            expected = DecodeConfig(colorSpace = ColorSpace.displayP3),
            actual = DecodeConfig(
                ImageRequest(context, "test.jpg") {
                    colorSpace(ColorSpace.displayP3)
                }
            )
        )
    }

    @Test
    fun testConstructor() {
        DecodeConfig().apply {
            assertEquals(null, sampleSize)
            assertEquals(null, colorType)
            assertEquals(null, colorSpace)
        }
        DecodeConfig(
            sampleSize = 2,
            colorType = ColorType.RGB_565,
            colorSpace = ColorSpace.sRGB
        ).apply {
            assertEquals(2, sampleSize)
            assertEquals(ColorType.RGB_565, colorType)
            assertEquals(ColorSpace.sRGB, colorSpace)
        }
    }

    @Test
    fun testFields() {
        DecodeConfig().apply {
            assertEquals(null, sampleSize)
            assertEquals(null, colorType)
            assertEquals(null, colorSpace)

            sampleSize = 2
            assertEquals(2, sampleSize)
            assertEquals(null, colorType)
            assertEquals(null, colorSpace)

            colorType = ColorType.RGB_565
            assertEquals(2, sampleSize)
            assertEquals(ColorType.RGB_565, colorType)
            assertEquals(null, colorSpace)

            colorSpace = ColorSpace.sRGB
            assertEquals(2, sampleSize)
            assertEquals(ColorType.RGB_565, colorType)
            assertEquals(ColorSpace.sRGB, colorSpace)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DecodeConfig()
        val element11 = DecodeConfig()
        val element2 = DecodeConfig(
            sampleSize = 2,
        )
        val element3 = DecodeConfig(
            sampleSize = 2,
            colorType = ColorType.RGB_565,
        )
        val element4 = DecodeConfig(
            sampleSize = 2,
            colorType = ColorType.RGB_565,
            colorSpace = ColorSpace.sRGB
        )

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "DecodeConfig(sampleSize=null, colorType=null, colorSpace=null)",
            actual = DecodeConfig().toString()
        )
        assertEquals(
            expected = "DecodeConfig(sampleSize=2, colorType=RGB_565, colorSpace=sRGB)",
            actual = DecodeConfig(
                sampleSize = 2,
                colorType = ColorType.RGB_565,
                colorSpace = ColorSpace.sRGB
            ).toString()
        )
    }
}