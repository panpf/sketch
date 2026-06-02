package com.github.panpf.sketch.core.ios.test.decode

import com.github.panpf.sketch.core.nonandroid.test.decode.SkiaDecoderTest.FullTestDataSource
import com.github.panpf.sketch.core.nonandroid.test.decode.SkiaDecoderTest.RegionTestDataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.UIImageDecoder
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.images.ComposeResImageFiles
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
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.defaultColorType
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UIImageDecoderTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        val requestContext = ImageRequest(context, imageFile.uri).toRequestContext(sketch)
        UIImageDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = imageFile.mimeType
        )
        UIImageDecoder(requestContext, dataSource, imageFile.mimeType)
    }

    @Test
    fun testGetImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        val requestContext = ImageRequest(context, imageFile.uri).toRequestContext(sketch)
        UIImageDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = imageFile.mimeType
        ).apply {
            assertEquals(
                expected = ImageInfo(size = imageFile.size, mimeType = imageFile.mimeType),
                actual = getImageInfo()
            )
        }
    }

    @Test
    fun testDecodeDefault() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = UIImageDecoder.Factory()

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,${defaultColorType.name},sRGB)",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,${defaultColorType.name},sRGB)",
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
        ComposeResImageFiles.clockExifs.forEach { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
            }.decode(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(
                    expected = "Bitmap(1500x750,${defaultColorType.name},sRGB)",
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
        val factory = UIImageDecoder.Factory()

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(ColorType.RGB_565)
        }.decode(sketch, factory).apply {
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

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(ColorType.RGB_565)
        }.decode(sketch, factory).apply {
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
    }

    @Test
    fun testDecodeColorSpace() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = UIImageDecoder.Factory()

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,${defaultColorType.name},sRGB)",
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

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,${defaultColorType.name},sRGB)",
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

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ColorSpace.displayP3)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,${defaultColorType.name},sRGB)",
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

        ImageRequest(context, ComposeResImageFiles.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ColorSpace.displayP3)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,${defaultColorType.name},sRGB)",
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
    }

    @Test
    fun testDecodeResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = UIImageDecoder.Factory()

        // precision = LESS_PIXELS
        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNotNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNotNull(actual = transformeds.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch, factory).image.getBitmapOrThrow()
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
        val factory = UIImageDecoder.Factory()

        // precision = LESS_PIXELS
        ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(200, 200)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
        }
        ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch, factory).apply {
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
        ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, ComposeResImageFiles.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch, factory).image.getBitmapOrThrow()
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
        val factory = UIImageDecoder.Factory()

        val testFile = ComposeResImageFiles.clockExifTranspose

        // precision = LESS_PIXELS
        ImageRequest(context, testFile.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNotNull(actual = transformeds.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch, factory).apply {
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
            assertNotNull(actual = transformeds.getSubsamplingTransformed())
            assertNotNull(actual = transformeds.getResizeTransformed())
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
        }.decode(sketch, factory).apply {
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
            assertNull(actual = transformeds.getSubsamplingTransformed())
            assertNull(actual = transformeds.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch, factory).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch, factory).image.getBitmapOrThrow()
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
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resize(
                ComposeResImageFiles.jpeg.size.width * 2,
                ComposeResImageFiles.jpeg.size.height * 2
            )
        }
        val dataSource = sketch.components.newFetcherOrThrow(
            request.toRequestContext(sketch, Size.Empty)
        ).fetch().getOrThrow().dataSource
        val bitmapDecoder = UIImageDecoder(
            requestContext = request.toRequestContext(sketch),
            dataSource = FullTestDataSource(dataSource.asOrThrow(), enabledCount = true),
            mimeType = ComposeResImageFiles.jpeg.mimeType
        )
        bitmapDecoder.decode()

        /* region */
        val request1 = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            size(500, 500)
            precision(EXACTLY)
        }
        val dataSource1 = sketch.components.newFetcherOrThrow(
            request1.toRequestContext(sketch, Size.Empty)
        ).fetch().getOrThrow().dataSource
        UIImageDecoder(
            requestContext = request1.toRequestContext(sketch),
            dataSource = RegionTestDataSource(dataSource1.asOrThrow(), false, enabledCount = true),
            mimeType = ComposeResImageFiles.jpeg.mimeType
        ).decode()
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)
        val element1 = UIImageDecoder(requestContext, dataSource, "image/jpeg")
        val element11 = UIImageDecoder(requestContext, dataSource, "image/jpeg")

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)
        val decoder = UIImageDecoder(requestContext, dataSource, "image/jpeg")
        assertTrue(
            actual = decoder.toString().contains("UIImageDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        UIImageDecoder.Factory()
        UIImageDecoder.SupplementSkiaFactory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "UIImageDecoder",
            actual = UIImageDecoder.Factory().key
        )
        assertEquals(
            expected = "SupplementSkiaUIImageDecoder",
            actual = UIImageDecoder.SupplementSkiaFactory().key
        )
    }

    @Test
    fun testFactorySortWeight() {
        assertEquals(
            expected = 99,
            actual = UIImageDecoder.Factory().sortWeight
        )
        assertEquals(
            expected = 99,
            actual = UIImageDecoder.SupplementSkiaFactory().sortWeight
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ComposeResImageFiles.statics.plus(ComposeResImageFiles.anims)
            .forEach { imageFile ->
                ImageRequest(context, imageFile.uri)
                    .createDecoderOrNull(sketch, UIImageDecoder.Factory()) {
                        it.copy(mimeType = it.mimeType)
                    }.apply {
                        assertTrue(this is UIImageDecoder)
                    }
            }

        ComposeResImageFiles.statics.plus(ComposeResImageFiles.anims)
            .forEach { imageFile ->
                ImageRequest(context, imageFile.uri)
                    .createDecoderOrNull(sketch, UIImageDecoder.SupplementSkiaFactory()) {
                        it.copy(mimeType = it.mimeType)
                    }.apply {
                        if (imageFile.name.contains("heif")
                            || imageFile.name.contains("heic")
                            || imageFile.name.contains("avif")
                        ) {
                            assertTrue(this is UIImageDecoder)
                            val imageInfo = this.getImageInfo()
                            assertTrue(imageInfo.mimeType == "image/heif" || imageInfo.mimeType == "image/avif")
                        } else {
                            assertNull(this)
                        }
                    }
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = UIImageDecoder.Factory()
        val element11 = UIImageDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryEqualsAndHashCode2() {
        val element1 = UIImageDecoder.SupplementSkiaFactory()
        val element11 = UIImageDecoder.SupplementSkiaFactory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "UIImageDecoder",
            actual = UIImageDecoder.Factory().toString()
        )
        assertEquals(
            expected = "SupplementSkiaUIImageDecoder",
            actual = UIImageDecoder.SupplementSkiaFactory().toString()
        )
    }
}