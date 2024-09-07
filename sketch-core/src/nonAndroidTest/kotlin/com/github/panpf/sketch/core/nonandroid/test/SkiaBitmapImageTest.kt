package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.SkiaBitmapImageTransformer
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.SkiaBitmapImageValue
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.createDecodeHelper
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.hammingDistance
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.toLogString
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SkiaBitmapImageTest {

    @Test
    fun testAsSketchImage() {
        SkiaBitmap(100, 100).asSketchImage()
    }

    @Test
    fun testConstructor() {
        val skiaBitmap = SkiaBitmap(100, 100)
        SkiaBitmapImage(skiaBitmap).apply {
            assertSame(expected = skiaBitmap, actual = bitmap)
            assertTrue(actual = shareable)
        }
        SkiaBitmapImage(skiaBitmap, shareable = false).apply {
            assertSame(expected = skiaBitmap, actual = bitmap)
            assertFalse(actual = shareable)
        }
    }

    @Test
    fun testWidthHeight() {
        SkiaBitmapImage(SkiaBitmap(100, 200)).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
        }
        SkiaBitmapImage(SkiaBitmap(200, 100)).apply {
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 100, actual = height)
        }
    }

    @Test
    fun testByteCount() {
        SkiaBitmapImage(SkiaBitmap(100, 200)).apply {
            assertEquals(expected = 100 * 200 * 4L, actual = byteCount)
            assertEquals(expected = 100 * 200 * 4L, actual = allocationByteCount)
        }
        SkiaBitmapImage(SkiaBitmap(200, 300)).apply {
            assertEquals(expected = 200 * 300 * 4L, actual = byteCount)
            assertEquals(expected = 200 * 300 * 4L, actual = allocationByteCount)
        }
    }

    @Test
    fun testCacheValue() {
        val bitmap = SkiaBitmap(100, 200)
        val extras = mapOf<String, Any?>("key" to 10)
        SkiaBitmapImage(bitmap).apply {
            assertEquals(expected = SkiaBitmapImageValue(this, null), actual = cacheValue())
            assertEquals(expected = SkiaBitmapImageValue(this, extras), actual = cacheValue(extras))
        }
    }

    @Test
    fun testCheckValid() {
        SkiaBitmapImage(SkiaBitmap(100, 200)).apply {
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
        }
    }

    @Test
    fun testTransformer() {
        SkiaBitmapImage(SkiaBitmap(100, 200)).apply {
            assertTrue(actual = transformer() is SkiaBitmapImageTransformer)
        }
    }

    @Test
    fun testGetPixels() {
        SkiaBitmapImage(SkiaBitmap(100, 200)).apply {
            val pixels = getPixels()
            assertNotNull(actual = pixels)
            assertEquals(expected = 20000, actual = pixels.size)
        }
    }

    @Test
    fun testToString() {
        val bitmap = SkiaBitmap(100, 200)
        assertEquals(
            "SkiaBitmapImage(bitmap=${bitmap.toLogString()}, shareable=true)",
            SkiaBitmapImage(bitmap).toString()
        )

        val bitmap2 = SkiaBitmap(200, 100)
        assertEquals(
            "SkiaBitmapImage(bitmap=${bitmap2.toLogString()}, shareable=false)",
            SkiaBitmapImage(bitmap2, shareable = false).toString()
        )
    }

    @Test
    fun testSkiaBitmapImageTransformer() {
        val context = getTestContext()
        val transformer = SkiaBitmapImageTransformer

        val imageFile = ResourceImages.jpeg
        val request = ImageRequest(context, imageFile.uri)
        val dataSource = imageFile.toDataSource(context)
        val decoderHelper = createDecodeHelper(request, dataSource)

        val imageFinger: String
        val image = decoderHelper.decode(1).asOrThrow<SkiaBitmapImage>().apply {
            assertEquals(expected = Size(1291, 1936), actual = size)
            imageFinger = produceFingerPrint(this.bitmap)
        }

        val upperScaleImageFinger: String
        transformer.scale(image, 1.5f).asOrThrow<SkiaBitmapImage>().apply {
            assertEquals(expected = Size(1937, 2904), actual = size)
            upperScaleImageFinger = produceFingerPrint(this.bitmap)
        }

        val lowerScaleImageFinger: String
        transformer.scale(image, 0.5f).asOrThrow<SkiaBitmapImage>().apply {
            assertEquals(expected = Size(646, 968), actual = size)
            lowerScaleImageFinger = produceFingerPrint(this.bitmap)
        }

        assertEquals(expected = imageFinger, actual = upperScaleImageFinger)
        assertEquals(expected = imageFinger, actual = lowerScaleImageFinger)
        assertEquals(expected = upperScaleImageFinger, actual = lowerScaleImageFinger)

        val bigSize = image.size.let { max(it.width, it.height) }.let { Size(it, it) }
        val resize1 = Resize(bigSize, Precision.SAME_ASPECT_RATIO, Scale.CENTER_CROP)
        val resize1Mapping = resize1.calculateMapping(image.size)
        val resize1MappingImageFinger: String
        transformer.mapping(image, resize1Mapping)
            .asOrThrow<SkiaBitmapImage>().apply {
                assertEquals(expected = Size(1291, 1291), actual = size)
                resize1MappingImageFinger = produceFingerPrint(this.bitmap)
            }

        val resize2 = Resize(bigSize, Precision.SAME_ASPECT_RATIO, Scale.START_CROP)
        val resize2Mapping = resize2.calculateMapping(image.size)
        val resize2MappingImageFinger: String
        transformer.mapping(image, resize2Mapping)
            .asOrThrow<SkiaBitmapImage>().apply {
                assertEquals(expected = Size(1291, 1291), actual = size)
                resize2MappingImageFinger = produceFingerPrint(this.bitmap)
            }

        val resize3 = Resize(bigSize, Precision.EXACTLY, Scale.CENTER_CROP)
        val resize3Mapping = resize3.calculateMapping(image.size)
        val resize3MappingImageFinger: String
        transformer.mapping(image, resize3Mapping)
            .asOrThrow<SkiaBitmapImage>().apply {
                assertEquals(expected = bigSize, actual = size)
                resize3MappingImageFinger = produceFingerPrint(this.bitmap)
            }

        val resize4 = Resize(bigSize, Precision.EXACTLY, Scale.START_CROP)
        val resize4Mapping = resize4.calculateMapping(image.size)
        val resize4MappingImageFinger: String
        transformer.mapping(image, resize4Mapping)
            .asOrThrow<SkiaBitmapImage>().apply {
                assertEquals(expected = bigSize, actual = size)
                resize4MappingImageFinger = produceFingerPrint(this.bitmap)
            }

        assertNotEquals(illegal = imageFinger, actual = resize1MappingImageFinger)
        assertNotEquals(illegal = imageFinger, actual = resize2MappingImageFinger)
        assertNotEquals(illegal = imageFinger, actual = resize3MappingImageFinger)
        assertNotEquals(illegal = imageFinger, actual = resize4MappingImageFinger)
        assertNotEquals(illegal = resize1MappingImageFinger, actual = resize2MappingImageFinger)
        assertEquals(expected = resize1MappingImageFinger, actual = resize3MappingImageFinger)
        assertNotEquals(illegal = resize2MappingImageFinger, actual = resize3MappingImageFinger)
        assertEquals(expected = resize2MappingImageFinger, actual = resize4MappingImageFinger)

        assertTrue(
            actual = hammingDistance(imageFinger, resize1MappingImageFinger) >= 5,
            message = "hammingDistance=${hammingDistance(imageFinger, resize1MappingImageFinger)}"
        )
        assertTrue(
            actual = hammingDistance(imageFinger, resize2MappingImageFinger) >= 5,
            message = "hammingDistance=${hammingDistance(imageFinger, resize2MappingImageFinger)}"
        )
        assertTrue(
            actual = hammingDistance(imageFinger, resize3MappingImageFinger) >= 5,
            message = "hammingDistance=${hammingDistance(imageFinger, resize3MappingImageFinger)}"
        )
        assertTrue(
            actual = hammingDistance(imageFinger, resize4MappingImageFinger) >= 5,
            message = "hammingDistance=${hammingDistance(imageFinger, resize4MappingImageFinger)}"
        )
        assertTrue(
            actual = hammingDistance(resize1MappingImageFinger, resize2MappingImageFinger) >= 5,
            message = "hammingDistance=${
                hammingDistance(
                    resize1MappingImageFinger,
                    resize2MappingImageFinger
                )
            }"
        )
        assertTrue(
            actual = hammingDistance(resize1MappingImageFinger, resize3MappingImageFinger) == 0,
            message = "hammingDistance=${
                hammingDistance(
                    resize1MappingImageFinger,
                    resize3MappingImageFinger
                )
            }"
        )
        assertTrue(
            actual = hammingDistance(resize2MappingImageFinger, resize3MappingImageFinger) >= 5,
            message = "hammingDistance=${
                hammingDistance(
                    resize2MappingImageFinger,
                    resize3MappingImageFinger
                )
            }"
        )
        assertTrue(
            actual = hammingDistance(resize2MappingImageFinger, resize4MappingImageFinger) == 0,
            message = "hammingDistance=${
                hammingDistance(
                    resize2MappingImageFinger,
                    resize4MappingImageFinger
                )
            }"
        )
    }
}