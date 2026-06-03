/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.avif.awxkee.test.decode

import android.graphics.ColorSpace
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.AwxkeeAvifDecoder
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.decode.supportAwxkeeAvif
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.colorSpaceNameCompat
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.createDecoder
import com.github.panpf.sketch.test.utils.decodeOrThrow
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.isSameAspectRatio
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isThumbnailWithSize
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AwxkeeAvifDecoderTest {

    @Test
    fun testSupportAwxkeeAvif() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAwxkeeAvif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[AwxkeeAvifDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAwxkeeAvif()
            supportAwxkeeAvif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[AwxkeeAvifDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testCompanion() {
        assertEquals(
            expected = 25,
            actual = AwxkeeAvifDecoder.SORT_WEIGHT
        )
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ComposeResImageFiles.avif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ComposeResImageFiles.avif.toDataSource(context)

        AwxkeeAvifDecoder(requestContext, dataSource, "image/avif")
        AwxkeeAvifDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/avif"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val (context, sketch) = getTestContextAndSketch()

        val avifImageFile = ComposeResImageFiles.avif
        AwxkeeAvifDecoder(
            requestContext = ImageRequest(context, avifImageFile.uri).toRequestContext(sketch),
            dataSource = avifImageFile.toDataSource(context),
            mimeType = avifImageFile.mimeType
        ).apply {
            assertEquals(
                expected = avifImageFile.imageInfo,
                actual = getImageInfo()
            )
        }

        val heifImageFile = ComposeResImageFiles.heic
        AwxkeeAvifDecoder(
            requestContext = ImageRequest(context, heifImageFile.uri).toRequestContext(sketch),
            dataSource = heifImageFile.toDataSource(context),
            mimeType = heifImageFile.mimeType
        ).apply {
            assertEquals(
                expected = heifImageFile.imageInfo,
                actual = getImageInfo()
            )
        }

        val jpegImageFile = ComposeResImageFiles.jpeg
        AwxkeeAvifDecoder(
            requestContext = ImageRequest(context, jpegImageFile.uri).toRequestContext(sketch),
            dataSource = jpegImageFile.toDataSource(context),
            mimeType = jpegImageFile.mimeType
        ).apply {
            assertFailsWith(Exception::class) {
                getImageInfo()
            }
        }
    }

    @Test
    fun testDecodeDefault() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val (context, sketch) = getTestContextAndSketch()
        val factory = AwxkeeAvifDecoder.Factory()

        ComposeResImageFiles.jpeg.also { imageFile ->
            assertFailsWith(IllegalArgumentException::class) {
                ImageRequest(context, imageFile.uri).decodeOrThrow(sketch, factory)
            }
        }

        ComposeResImageFiles.avif.also { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(width = imageFile.size.width * 2, height = imageFile.size.height * 2)
                precision(Precision.LESS_PIXELS)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(imageFile.size, bitmap.size)
                assertEquals(ColorType.ARGB_8888, bitmap.colorType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals("SRGB", bitmap.colorSpaceNameCompat)
                }
                assertEquals(expected = imageFile.size, actual = imageInfo.size)
                assertEquals(expected = imageFile.mimeType, actual = imageInfo.mimeType)
                assertEquals(expected = LOCAL, actual = dataFrom)
                assertNull(actual = transformeds)
            }
        }

        ComposeResImageFiles.heic.also { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(width = imageFile.size.width * 2, height = imageFile.size.height * 2)
                precision(Precision.LESS_PIXELS)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(imageFile.size, bitmap.size)
                assertEquals(ColorType.ARGB_8888, bitmap.colorType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals("SRGB", bitmap.colorSpaceNameCompat)
                }
                assertEquals(expected = imageFile.size, actual = imageInfo.size)
                assertEquals(expected = imageFile.mimeType, actual = imageInfo.mimeType)
                assertEquals(expected = LOCAL, actual = dataFrom)
                assertNull(actual = transformeds)
            }
        }
    }

    @Test
    fun testDecodeColorType() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val (context, sketch) = getTestContextAndSketch()
        val factory = AwxkeeAvifDecoder.Factory()

        ComposeResImageFiles.avif.also { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(width = imageFile.size.width * 2, height = imageFile.size.height * 2)
                precision(Precision.LESS_PIXELS)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(ColorType.ARGB_8888, bitmap.colorType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals("SRGB", bitmap.colorSpaceNameCompat)
                }
            }
        }

        ComposeResImageFiles.avif.also { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(width = imageFile.size.width * 2, height = imageFile.size.height * 2)
                precision(Precision.LESS_PIXELS)
                colorType(ColorType.RGB_565)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(ColorType.RGB_565, bitmap.colorType)
            }
        }
    }

    @Test
    fun testDecodeColorSpace() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val (context, sketch) = getTestContextAndSketch()
        val factory = AwxkeeAvifDecoder.Factory()

        ComposeResImageFiles.avif.also { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(width = imageFile.size.width * 2, height = imageFile.size.height * 2)
                precision(Precision.LESS_PIXELS)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals("SRGB", bitmap.colorSpaceNameCompat)
                }
            }
        }

        ComposeResImageFiles.avif.also { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(width = imageFile.size.width * 2, height = imageFile.size.height * 2)
                precision(Precision.LESS_PIXELS)
                colorSpace(ColorSpace.Named.DISPLAY_P3)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals("SRGB", bitmap.colorSpaceNameCompat)
                }
            }
        }
    }

    @Test
    fun testDecodeResize() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val (context, sketch) = getTestContextAndSketch()
        val factory = AwxkeeAvifDecoder.Factory()

        // precision = LESS_PIXELS
        ComposeResImageFiles.avif.also { imageFile ->
            val resize = Size(500, 500)
            val precision = Precision.LESS_PIXELS
            ImageRequest(context, imageFile.uri) {
                size(resize)
                precision(precision)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertTrue(isThumbnailWithSize(size = bitmap.size, otherSize = imageFile.size))
                assertTrue(isSameAspectRatio(size = bitmap.size, otherSize = imageFile.size))
                assertFalse(isSameAspectRatio(size = bitmap.size, otherSize = resize))
                assertEquals(2f, imageFile.size.width / bitmap.width.toFloat(), 0.1f)
                assertEquals(2f, imageFile.size.height / bitmap.height.toFloat(), 0.1f)
                assertNotNull(transformeds?.getInSampledTransformed())
                assertNull(transformeds?.getSubsamplingTransformed())
                assertNull(transformeds?.getResizeTransformed())
            }
        }

        // precision = SMALLER_SIZE
        ComposeResImageFiles.avif.also { imageFile ->
            val resize = Size(500, 500)
            val precision = Precision.SMALLER_SIZE
            ImageRequest(context, imageFile.uri) {
                size(resize)
                precision(precision)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertTrue(isThumbnailWithSize(size = bitmap.size, otherSize = imageFile.size))
                assertTrue(isSameAspectRatio(size = bitmap.size, otherSize = imageFile.size))
                assertFalse(isSameAspectRatio(size = bitmap.size, otherSize = resize))
                assertEquals(4f, imageFile.size.width / bitmap.width.toFloat(), 0.1f)
                assertEquals(4f, imageFile.size.height / bitmap.height.toFloat(), 0.1f)
                assertNotNull(transformeds?.getInSampledTransformed())
                assertNull(transformeds?.getSubsamplingTransformed())
                assertNull(transformeds?.getResizeTransformed())
            }
        }

        // precision = SAME_ASPECT_RATIO
        ComposeResImageFiles.avif.also { imageFile ->
            val resize = Size(500, 500)
            val precision = Precision.SAME_ASPECT_RATIO
            ImageRequest(context, imageFile.uri) {
                size(resize)
                precision(precision)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertFalse(isThumbnailWithSize(size = bitmap.size, otherSize = imageFile.size))
                assertFalse(isSameAspectRatio(size = bitmap.size, otherSize = imageFile.size))
                assertTrue(isSameAspectRatio(size = bitmap.size, otherSize = resize))
                assertEquals(3f, imageFile.size.width / bitmap.width.toFloat(), 0.1f)
                assertEquals(2f, imageFile.size.height / bitmap.height.toFloat(), 0.1f)
                assertNotNull(transformeds?.getInSampledTransformed())
                assertNull(transformeds?.getSubsamplingTransformed())
                assertNotNull(transformeds?.getResizeTransformed())
            }
        }

        // precision = EXACTLY
        ComposeResImageFiles.avif.also { imageFile ->
            val resize = Size(500, 500)
            val precision = Precision.EXACTLY
            ImageRequest(context, imageFile.uri) {
                size(resize)
                precision(precision)
            }.decodeOrThrow(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertFalse(isThumbnailWithSize(size = bitmap.size, otherSize = imageFile.size))
                assertFalse(isSameAspectRatio(size = bitmap.size, otherSize = imageFile.size))
                assertTrue(isSameAspectRatio(size = bitmap.size, otherSize = resize))
                assertEquals(resize, bitmap.size)
                assertEquals(2.4f, imageFile.size.width / bitmap.width.toFloat(), 0.1f)
                assertEquals(1.6f, imageFile.size.height / bitmap.height.toFloat(), 0.1f)
                assertNotNull(transformeds?.getInSampledTransformed())
                assertNull(transformeds?.getSubsamplingTransformed())
                assertNotNull(transformeds?.getResizeTransformed())
            }
        }

        // scale
        val startCropBitmap = ImageRequest(context, ComposeResImageFiles.avif.uri) {
            size(Size(500, 500))
            precision(Precision.EXACTLY)
            scale(Scale.START_CROP)
        }.decodeOrThrow(sketch, factory).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, ComposeResImageFiles.avif.uri) {
            size(Size(500, 500))
            precision(Precision.EXACTLY)
            scale(Scale.CENTER_CROP)
        }.decodeOrThrow(sketch, factory).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, ComposeResImageFiles.avif.uri) {
            size(Size(500, 500))
            precision(Precision.EXACTLY)
            scale(Scale.END_CROP)
        }.decodeOrThrow(sketch, factory).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, ComposeResImageFiles.avif.uri) {
            size(Size(500, 500))
            precision(Precision.EXACTLY)
            scale(Scale.FILL)
        }.decodeOrThrow(sketch, factory).image.getBitmapOrThrow()
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
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ComposeResImageFiles.avif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ComposeResImageFiles.avif.toDataSource(context)
        val element1 = AwxkeeAvifDecoder(requestContext, dataSource, "image/avif")
        val element11 = AwxkeeAvifDecoder(requestContext, dataSource, "image/avif")

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ComposeResImageFiles.avif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ComposeResImageFiles.avif.toDataSource(context)
        val decoder = AwxkeeAvifDecoder(requestContext, dataSource, "image/avif")
        assertTrue(actual = decoder.toString().contains("AwxkeeAvifDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryConstructor() {
        AwxkeeAvifDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "AwxkeeAvifDecoder",
            actual = AwxkeeAvifDecoder.Factory().key
        )
    }

    @Test
    fun testFactorySortWeight() {
        assertEquals(
            expected = 25,
            actual = AwxkeeAvifDecoder.Factory().sortWeight
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AwxkeeAvifDecoder.Factory()

        assertNotNull(
            ImageRequest(context, ComposeResImageFiles.avif.uri)
                .createDecoder(sketch, factory)
        )

        assertNotNull(
            ImageRequest(context, ComposeResImageFiles.heic.uri)
                .createDecoder(sketch, factory)
        )

        assertNull(
            ImageRequest(context, ComposeResImageFiles.jpeg.uri)
                .createDecoder(sketch, factory)
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AwxkeeAvifDecoder.Factory()
        val element11 = AwxkeeAvifDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "AwxkeeAvifDecoder",
            actual = AwxkeeAvifDecoder.Factory().toString()
        )
    }
}