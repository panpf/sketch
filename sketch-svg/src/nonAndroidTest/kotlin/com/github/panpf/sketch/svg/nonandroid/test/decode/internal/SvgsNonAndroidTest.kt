package com.github.panpf.sketch.svg.nonandroid.test.decode.internal

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.decode.internal.decodeSvg
import com.github.panpf.sketch.decode.internal.readSvgImageInfo
import com.github.panpf.sketch.decode.name
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SvgsNonAndroidTest {

    @Test
    fun testReadSvgImageInfo() {
        val context = getTestContext()
        val dataSource = ResourceImages.svg.toDataSource(context)
        assertEquals(
            expected = "ImageInfo(256x225,'image/svg+xml')",
            actual = dataSource.readSvgImageInfo(useViewBoundsAsIntrinsicSize = false)
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(257x226,'image/svg+xml')",
            actual = dataSource.readSvgImageInfo(useViewBoundsAsIntrinsicSize = true)
                .toShortString()
        )

        // RuntimeException: Can't wrap nullptr
        assertFailsWith(RuntimeException::class) {
            ResourceImages.png.toDataSource(context)
                .readSvgImageInfo()
        }
    }

    @Test
    fun testDecodeSvg() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageFile = ResourceImages.svg

        val defaultConfigBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                    }.toRequestContext(sketch)
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(257, 226), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGBA_8888, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.sRGB.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap
        val useViewBoundsAsIntrinsicSizeFalseBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                    }.toRequestContext(sketch),
                    useViewBoundsAsIntrinsicSize = false
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(256x225,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(256, 225), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGBA_8888, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.sRGB.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap
        val resizeBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(500, 500)
                    }.toRequestContext(sketch),
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(500, 440), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGBA_8888, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.sRGB.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap
        val resize2Bitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(100, 100)
                    }.toRequestContext(sketch),
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(100, 88), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGBA_8888, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.sRGB.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap
        val colorTypeBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                        colorType(ColorType.RGB_565)
                    }.toRequestContext(sketch),
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(257, 226), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGB_565, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.sRGB.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap
        val colorSpaceBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                        colorSpace(ColorSpace.displayP3)
                    }.toRequestContext(sketch),
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(257, 226), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGBA_8888, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.displayP3.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap
        val backgroundBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                        svgBackgroundColor(TestColor.RED)
                    }.toRequestContext(sketch),
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(257, 226), actual = bitmap.size)
                    assertEquals(expected = ColorType.RGBA_8888, actual = bitmap.colorType)
                    assertEquals(
                        expected = ColorSpace.sRGB.name(),
                        actual = bitmap.colorSpace?.name()
                    )
                }.image.asOrThrow<BitmapImage>().bitmap

        // Example svg image does not support css
        defaultConfigBitmap.similarity(useViewBoundsAsIntrinsicSizeFalseBitmap).also { similarity ->
            assertTrue(similarity <= 2, "similarity=$similarity")
        }
        defaultConfigBitmap.similarity(resizeBitmap).also { similarity ->
            assertTrue(similarity == 0, "similarity=$similarity")
        }
        defaultConfigBitmap.similarity(resize2Bitmap).also { similarity ->
            assertTrue(similarity == 0, "similarity=$similarity")
        }
        defaultConfigBitmap.similarity(colorTypeBitmap).also { similarity ->
            assertTrue(similarity <= 2, "similarity=$similarity")
        }
        defaultConfigBitmap.similarity(colorSpaceBitmap).also { similarity ->
            assertTrue(similarity <= 2, "similarity=$similarity")
        }
        defaultConfigBitmap.similarity(backgroundBitmap).also { similarity ->
            assertTrue(similarity == 0, "similarity=$similarity")
        }
        assertEquals(
            expected = listOf(0, 0, 0, 0),
            actual = defaultConfigBitmap.corners()
        )
        assertEquals(
            expected = listOf(TestColor.RED, TestColor.RED, TestColor.RED, TestColor.RED),
            actual = backgroundBitmap.corners()
        )

        // RuntimeException: Can't wrap nullptr
        assertFailsWith(RuntimeException::class) {
            ResourceImages.png.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, ResourceImages.png.uri) {
                        size(Size.Origin)
                    }.toRequestContext(sketch)
                )
        }
    }
}