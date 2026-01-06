package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.transform.createRotateTransformed
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class ImageDataTest {

    @Test
    fun testConstructor() {
        ImageData(
            image = FakeImage(100, 100),
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(200, 200),
            transformeds = listOf(createScaledTransformed(1.5f)),
            extras = mapOf("key" to "value"),
        )

        ImageData(
            FakeImage(100, 100),
            ImageInfo(100, 100, "image/jpeg"),
            LOCAL,
            Resize(200, 200),
            listOf(createScaledTransformed(1.5f)),
            mapOf("key" to "value"),
        )
    }

    @Test
    fun testNewResult() {
        val image1 = createBitmapImage(100, 100)
        val image2 = createBitmapImage(200, 200)

        val result = ImageData(
            image = image1,
            imageInfo = ImageInfo(3000, 500, "image/png"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
            transformeds = listOf(createInSampledTransformed(4), createRotateTransformed(45)),
            extras = mapOf("age" to "16"),
        ).apply {
            assertEquals(image1, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newImageData().apply {
            assertNotSame(result, this)
            assertEquals(result, this)
            assertEquals(image1, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newImageData(image = image2).apply {
            assertNotSame(result, this)
            assertNotEquals(result, this)
            assertEquals(image2, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newImageData(imageInfo = result.imageInfo.copy(size = Size(200, 200)))
            .apply {
                assertNotSame(result, this)
                assertNotEquals(result, this)
                assertEquals(image1, image)
                assertEquals(ImageInfo(200, 200, "image/png"), imageInfo)
                assertEquals(expected = LOCAL, actual = dataFrom)
                assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
                assertEquals(
                    listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                    transformeds
                )
                assertEquals(
                    mapOf("age" to "16"),
                    this.extras
                )
            }

        result.newImageData(dataFrom = MEMORY).apply {
            assertNotSame(result, this)
            assertNotEquals(result, this)
            assertEquals(image1, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(MEMORY, dataFrom)
            assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newImageData {
            addTransformed(createCircleCropTransformed(FILL))
        }.apply {
            assertNotSame(result, this)
            assertNotEquals(result, this)
            assertEquals(image1, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            assertEquals(
                listOf(
                    createInSampledTransformed(4),
                    createRotateTransformed(45),
                    createCircleCropTransformed(FILL)
                ),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newImageData {
            addExtras("sex", "male")
        }.apply {
            assertNotSame(result, this)
            assertNotEquals(result, this)
            assertEquals(image1, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16", "sex" to "male"),
                this.extras
            )
        }

        result.newImageData(resize = Resize(200, 300, EXACTLY, FILL)).apply {
            assertNotSame(result, this)
            assertNotEquals(result, this)
            assertEquals(image1, image)
            assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(Resize(200, 300, EXACTLY, FILL), resize)
            assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ImageData(
            image = FakeImage(100, 100),
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(200, 200),
            transformeds = listOf(createScaledTransformed(1.5f)),
            extras = mapOf("key" to "value"),
        )
        val element11 = element1.copy()
        val element2 = element1.copy(image = FakeImage(200, 200))
        val element3 = element1.copy(imageInfo = ImageInfo(200, 200, "image/png"))
        val element4 = element1.copy(dataFrom = DataFrom.NETWORK)
        val element5 = element1.copy(resize = Resize(300, 300))
        val element6 = element1.copy(transformeds = listOf(createScaledTransformed(2.0f)))
        val element7 = element1.copy(extras = mapOf("key" to "value2"))

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element1, actual = element4)
        assertNotEquals(illegal = element1, actual = element5)
        assertNotEquals(illegal = element1, actual = element6)
        assertNotEquals(illegal = element1, actual = element7)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element2, actual = element4)
        assertNotEquals(illegal = element2, actual = element5)
        assertNotEquals(illegal = element2, actual = element6)
        assertNotEquals(illegal = element2, actual = element7)
        assertNotEquals(illegal = element3, actual = element4)
        assertNotEquals(illegal = element3, actual = element5)
        assertNotEquals(illegal = element3, actual = element6)
        assertNotEquals(illegal = element3, actual = element7)
        assertNotEquals(illegal = element4, actual = element5)
        assertNotEquals(illegal = element4, actual = element6)
        assertNotEquals(illegal = element4, actual = element7)
        assertNotEquals(illegal = element5, actual = element6)
        assertNotEquals(illegal = element5, actual = element7)
        assertNotEquals(illegal = element6, actual = element7)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element5.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element4.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element5.hashCode(), actual = element6.hashCode())
        assertNotEquals(illegal = element5.hashCode(), actual = element7.hashCode())
        assertNotEquals(illegal = element6.hashCode(), actual = element7.hashCode())
    }

    @Test
    fun testToString() {
        val element1 = ImageData(
            image = FakeImage(100, 100),
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(200, 200),
            transformeds = listOf(createScaledTransformed(1.5f)),
            extras = mapOf("key" to "value"),
        )
        assertEquals(
            expected = "ImageData(image=FakeImage(size=100x100), imageInfo=ImageInfo(size=100x100, mimeType='image/jpeg'), dataFrom=LOCAL, resize=Resize(size=200x200, precision=LESS_PIXELS, scale=CENTER_CROP), transformeds=[ScaledTransformed(1.5)], extras={key=value})",
            actual = element1.toString()
        )
    }
}