package com.github.panpf.sketch.svg.android.test.decode.internal

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.internal.decodeSvg
import com.github.panpf.sketch.decode.internal.readSvgImageInfo
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class SvgsAndroidTest {

    @Test
    fun testReadSvgImageInfo() {
        val context = getTestContext()
        val dataSource = ResourceImages.svg.toDataSource(context)
        assertEquals(
            expected = "ImageInfo(257x226,'image/svg+xml')",
            actual = dataSource.readSvgImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(256x225,'image/svg+xml')",
            actual = dataSource.readSvgImageInfo(useViewBoundsAsIntrinsicSize = false)
                .toShortString()
        )

        // XmlPullParserException: Unexpected token
        assertFails {
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
                    assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace
                        )
                    }
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
                    assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace
                        )
                    }
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
                    assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace
                        )
                    }
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
                    assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace
                        )
                    }
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
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace
                        )
                    }
                }.image.asOrThrow<BitmapImage>().bitmap
        val colorSpaceBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                        if (VERSION.SDK_INT >= VERSION_CODES.O) {
                            colorSpace(ColorSpace.Named.DISPLAY_P3)
                        }
                    }.toRequestContext(sketch),
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(257, 226), actual = bitmap.size)
                    assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                            actual = bitmap.colorSpace
                        )
                    }
                }.image.asOrThrow<BitmapImage>().bitmap
        val backgroundBitmap =
            imageFile.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, imageFile.uri) {
                        size(Size.Origin)
                    }.toRequestContext(sketch),
                    backgroundColor = TestColor.RED
                ).apply {
                    assertEquals(
                        expected = "ImageInfo(257x226,'image/svg+xml')",
                        actual = imageInfo.toShortString()
                    )
                    val bitmap = image.asOrThrow<BitmapImage>().bitmap
                    assertEquals(expected = Size(257, 226), actual = bitmap.size)
                    assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace
                        )
                    }
                }.image.asOrThrow<BitmapImage>().bitmap

        // Example svg image does not support css
        defaultConfigBitmap.similarity(useViewBoundsAsIntrinsicSizeFalseBitmap).also { similarity ->
            assertTrue(similarity == 0, "similarity=$similarity")
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
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertTrue(similarity <= 2, "similarity=$similarity")
            } else {
                assertTrue(similarity == 0, "similarity=$similarity")
            }
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

        // XmlPullParserException: Unexpected token
        assertFails {
            ResourceImages.png.toDataSource(context)
                .decodeSvg(
                    requestContext = ImageRequest(context, ResourceImages.png.uri) {
                        size(Size.Origin)
                    }.toRequestContext(sketch)
                )
        }
    }
}