package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.utils.FakeImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ImageDataTest {

    @Test
    fun testConstructor() {
        ImageData(
            image = FakeImage(100, 100),
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = DataFrom.LOCAL,
            resize = Resize(200, 200),
            transformeds = listOf(createScaledTransformed(1.5f)),
            extras = mapOf("key" to "value"),
        )

        ImageData(
            FakeImage(100, 100),
            ImageInfo(100, 100, "image/jpeg"),
            DataFrom.LOCAL,
            Resize(200, 200),
            listOf(createScaledTransformed(1.5f)),
            mapOf("key" to "value"),
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ImageData(
            image = FakeImage(100, 100),
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = DataFrom.LOCAL,
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
            dataFrom = DataFrom.LOCAL,
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