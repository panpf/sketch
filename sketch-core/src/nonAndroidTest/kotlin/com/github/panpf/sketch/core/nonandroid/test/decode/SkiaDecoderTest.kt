package com.github.panpf.sketch.core.nonandroid.test.decode

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.SkiaDecoder
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.createDecoderOrDefault
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.test.runTest
import okio.Source
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SkiaDecoderTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.jpeg.toDataSource(context)

        SkiaDecoder(requestContext, dataSource)
        SkiaDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = SkiaDecoder.Factory()

        ResourceImages.statics.forEach { imageFile ->
            try {
                ImageRequest(context, imageFile.uri)
                    .createDecoderOrDefault(sketch, factory)
                    .apply {
                        assertSizeEquals(
                            expected = imageFile.size,
                            actual = imageInfo.size,
                            delta = Size(1, 1)
                        )
                        assertEquals(expected = imageFile.mimeType, actual = imageInfo.mimeType)
                    }
            } catch (e: IllegalArgumentException) {
                // IllegalArgumentException: Unsupported format
                e.printStackTrace()
            }
        }
    }

    @Test
    fun testDecodeDefault() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,RGBA_8888,sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1080x1344,'image/webp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }

        // exif
        ResourceImages.clockExifs.forEach { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
            }.decode(sketch).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(
                    expected = "Bitmap(1500x750,RGBA_8888,sRGB)",
                    actual = bitmap.toShortInfoString()
                )
                assertEquals(
                    expected = "ImageInfo(1500x750,'image/jpeg')",
                    actual = imageInfo.toShortString()
                )
                assertEquals(expected = LOCAL, actual = dataFrom)
                assertNull(actual = transformeds)
            }
        }
    }

    @Test
    fun testDecodeColorType() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(ColorType.RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,RGB_565,sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(ColorType.RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,RGB_565,sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1080x1344,'image/webp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }
    }

    @Test
    fun testDecodeColorSpace() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.sRGB, actual = bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,RGBA_8888,sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1080x1344,'image/webp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.sRGB, actual = bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ColorSpace.displayP3)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,displayP3)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.displayP3, actual = bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ColorSpace.displayP3)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,RGBA_8888,displayP3)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1080x1344,'image/webp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.displayP3, actual = bitmap.colorSpace)
        }
    }

    @Test
    fun testDecodeResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // precision = LESS_PIXELS
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = (bitmap.width * bitmap.height) <= (800 * 800 * 1.1f),
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(646, 968),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = (bitmap.width * bitmap.height) <= (500 * 500 * 1.1f),
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(323, 484),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = (bitmap.width * bitmap.height) <= (500 * 300 * 1.1f),
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = 500f.div(300).format(1)
            )
            assertSizeEquals(
                expected = Size(322, 193),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = (bitmap.width * bitmap.height) <= (300 * 500 * 1.1f),
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = 300f.div(500).format(1)
            )
            assertSizeEquals(
                expected = Size(290, 484),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = (bitmap.width * bitmap.height) <= (500 * 300 * 1.1f),
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(500, 300),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNotNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(300, 500),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNotNull(actual = transformeds?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch).image.getBitmapOrThrow()
        assertTrue(actual = startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        assertNotEquals(
            illegal = startCropBitmap.corners().toString(),
            actual = centerCropBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = startCropBitmap.corners().toString(),
            actual = endCropBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = startCropBitmap.corners().toString(),
            actual = fillBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = centerCropBitmap.corners().toString(),
            actual = endCropBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = centerCropBitmap.corners().toString(),
            actual = fillBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = endCropBitmap.corners().toString(),
            actual = fillBitmap.corners().toString()
        )
    }

    @Test
    fun testDecodeResizeNoRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // precision = LESS_PIXELS
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                (bitmap.width * bitmap.height) <= (500 * 500 * 1.1f),
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(350, 506),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(700x1012,'image/bmp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(200, 200)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                (bitmap.width * bitmap.height) <= (200 * 200 * 1.1f),
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(87, 126),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(700x1012,'image/bmp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = 500f.div(300).format(1)
            )
            assertSizeEquals(
                expected = Size(350, 210),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(700x1012,'image/bmp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
        }
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = 300f.div(500).format(1)
            )
            assertSizeEquals(
                expected = Size(152, 253),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(700x1012,'image/bmp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(500, 300),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(700x1012,'image/bmp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(300, 500),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(700x1012,'image/bmp')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch).image.getBitmapOrThrow()
        assertTrue(actual = startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        assertNotEquals(
            illegal = startCropBitmap.corners().toString(),
            actual = centerCropBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = startCropBitmap.corners().toString(),
            actual = endCropBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = startCropBitmap.corners().toString(),
            actual = fillBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = centerCropBitmap.corners().toString(),
            actual = endCropBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = centerCropBitmap.corners().toString(),
            actual = fillBitmap.corners().toString()
        )
        assertNotEquals(
            illegal = endCropBitmap.corners().toString(),
            actual = fillBitmap.corners().toString()
        )
    }

    @Test
    fun testDecodeResizeExif() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val testFile = ResourceImages.clockExifTranspose

        // precision = LESS_PIXELS
        ImageRequest(context, testFile.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 800 * 800 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(750, 375),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 500 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(375, 188),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = 500f.div(300).format(1)
            )
            assertSizeEquals(
                expected = Size(313, 188),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                expected = bitmap.width.toFloat().div(bitmap.height).format(1),
                actual = 300f.div(500).format(1)
            )
            assertSizeEquals(
                expected = Size(225, 375),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(500, 300),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNotNull(actual = transformeds?.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(300, 500),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNotNull(actual = transformeds?.getSubsamplingTransformed())
            assertNotNull(actual = transformeds?.getResizeTransformed())
        }

        // precision = LongImagePrecisionDecider
        ImageRequest(context, testFile.uri) {
            size(300, 400)
            precision(
                LongImagePrecisionDecider(
                    longImageDecider = DefaultLongImageDecider(
                        sameDirectionMultiple = 1f,
                        notSameDirectionMultiple = 5f
                    )
                )
            )
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(375, 188),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                expected = "ImageInfo(1500x750,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNotNull(actual = transformeds?.getInSampledTransformed())
            assertNull(actual = transformeds?.getSubsamplingTransformed())
            assertNull(actual = transformeds?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch).image.getBitmapOrThrow()
        assertTrue(actual = startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(actual = fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        assertEquals(
            expected = 8,
            actual = startCropBitmap.similarity(centerCropBitmap),
        )
        assertEquals(
            expected = 11,
            actual = startCropBitmap.similarity(endCropBitmap)
        )
        assertEquals(
            expected = 9,
            actual = startCropBitmap.similarity(fillBitmap)
        )
        assertEquals(
            expected = 10,
            actual = centerCropBitmap.similarity(endCropBitmap)
        )
        assertEquals(
            expected = 7,
            actual = centerCropBitmap.similarity(fillBitmap)
        )
        assertEquals(
            expected = 9,
            actual = endCropBitmap.similarity(fillBitmap)
        )
    }

    @Test
    fun testDecodeError() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* full */
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            resize(ResourceImages.jpeg.size.width * 2, ResourceImages.jpeg.size.height * 2)
        }
        val dataSource = sketch.components.newFetcherOrThrow(
            request.toRequestContext(sketch, Size.Empty)
        ).fetch().getOrThrow().dataSource
        val bitmapDecoder = SkiaDecoder(
            requestContext = request.toRequestContext(sketch),
            dataSource = FullTestDataSource(dataSource.asOrThrow(), enabledCount = true)
        )
        bitmapDecoder.decode()

        /* region */
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 500)
            precision(EXACTLY)
        }
        val dataSource1 = sketch.components.newFetcherOrThrow(
            request1.toRequestContext(sketch, Size.Empty)
        ).fetch().getOrThrow().dataSource
        SkiaDecoder(
            request1.toRequestContext(sketch),
            RegionTestDataSource(dataSource1.asOrThrow(), false, enabledCount = true)
        ).decode()
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.jpeg.toDataSource(context)
        val element1 = SkiaDecoder(requestContext, dataSource)
        val element11 = SkiaDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.jpeg.toDataSource(context)
        val decoder = SkiaDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("SkiaDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryConstructor() {
        SkiaDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "SkiaDecoder",
            actual = SkiaDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = SkiaDecoder.Factory()

        ResourceImages.statics.plus(ResourceImages.anims)
            .forEach { imageFile ->
                ImageRequest(context, imageFile.uri)
                    .createDecoderOrNull(sketch, factory) {
                        it.copy(mimeType = it.mimeType)
                    }.apply {
                        assertTrue(this is SkiaDecoder)
                    }
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = SkiaDecoder.Factory()
        val element11 = SkiaDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "SkiaDecoder",
            actual = SkiaDecoder.Factory().toString()
        )
    }

    class FullTestDataSource(
        private val fileDataSource: DataSource,
        private val enabledCount: Boolean = false,
    ) : DataSource by fileDataSource {

        private var count = 0

        @WorkerThread
        override fun openSource(): Source {
            val stackStringList = Exception().stackTraceToString().split("\n")
            if (stackStringList.find { it.contains(".realDecodeFull(") } != null) {
                count++
                if (!enabledCount || count == 1) {
                    throw IllegalArgumentException("Problem decoding into existing bitmap")
                }
            }
            return fileDataSource.openSource()
        }
    }

    class RegionTestDataSource(
        private val fileDataSource: DataSource,
        private val srcError: Boolean? = false,
        private val enabledCount: Boolean = false,
    ) : DataSource by fileDataSource {

        private var count = 0

        override fun openSource(): Source {
            val stackStringList = Exception().stackTraceToString().split("\n")
            if (stackStringList.find { it.contains(".realDecodeRegion(") } != null) {
                when (srcError) {
                    true -> {
                        throw IllegalArgumentException("rectangle is outside the image srcRect")
                    }

                    false -> {
                        count++
                        if (!enabledCount || count == 1) {
                            throw IllegalArgumentException("Problem decoding into existing bitmap")
                        }
                    }

                    else -> {
                        throw Exception()
                    }
                }
            }
            return fileDataSource.openSource()
        }
    }
}