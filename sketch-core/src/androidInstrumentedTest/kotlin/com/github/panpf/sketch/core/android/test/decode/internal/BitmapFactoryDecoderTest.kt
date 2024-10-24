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

package com.github.panpf.sketch.core.android.test.decode.internal

import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.SRGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.WorkerThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
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
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.test.runTest
import okio.Source
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapFactoryDecoderTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.jpeg.toDataSource(context)

        BitmapFactoryDecoder(requestContext, dataSource)
        BitmapFactoryDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = BitmapFactoryDecoder.Factory()

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
            } catch (e: ImageInvalidException) {
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
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
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
                expected = "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'image/webp')",
                    actual = imageInfo.toShortString()
                )
            } else {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'')",
                    actual = imageInfo.toShortString()
                )
            }
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
                    expected = "Bitmap(1500x750,ARGB_8888${shortInfoColorSpace("SRGB")})",
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
            colorType(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,RGB_565${shortInfoColorSpace("SRGB")})",
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
            colorType(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,RGB_565${shortInfoColorSpace("SRGB")})",
                actual = bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'image/webp')",
                    actual = imageInfo.toShortString()
                )
            } else {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'')",
                    actual = imageInfo.toShortString()
                )
            }
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }
    }

    @Test
    fun testDecodeColorSpace() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.O) return@runTest

        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.get(SRGB), actual = bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'image/webp')",
                    actual = imageInfo.toShortString()
                )
            } else {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'')",
                    actual = imageInfo.toShortString()
                )
            }
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.get(SRGB), actual = bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ADOBE_RGB)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("ADOBE_RGB")})",
                actual = bitmap.toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(1291x1936,'image/jpeg')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.get(ADOBE_RGB), actual = bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ADOBE_RGB)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                expected = "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpace("ADOBE_RGB")})",
                actual = bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'image/webp')",
                    actual = imageInfo.toShortString()
                )
            } else {
                assertEquals(
                    expected = "ImageInfo(1080x1344,'')",
                    actual = imageInfo.toShortString()
                )
            }
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(expected = ColorSpace.get(ADOBE_RGB), actual = bitmap.colorSpace)
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
                actual = bitmap.width * bitmap.height <= 800 * 800 * 1.1f,
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
                actual = bitmap.width * bitmap.height <= 500 * 500 * 1.1f,
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
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
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
                actual = bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
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
                actual = bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
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
                actual = bitmap.width * bitmap.height <= 500 * 500 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
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
                actual = bitmap.width * bitmap.height <= 200 * 200 * 1.1f,
                message = "${bitmap.width}x${bitmap.height}"
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
                expected = Size(175, 105),
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
            val longImageDecider = DefaultLongImageDecider(
                sameDirectionMultiple = 1f,
                notSameDirectionMultiple = 5f
            )
            precision(LongImagePrecisionDecider(longImageDecider = longImageDecider))
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
        startCropBitmap.similarity(centerCropBitmap).also { similarity ->
            assertTrue(
                actual = similarity >= 10,
                message = "similarity = $similarity"
            )
        }
        startCropBitmap.similarity(endCropBitmap).also { similarity ->
            assertTrue(
                actual = similarity >= 10,
                message = "similarity = $similarity"
            )
        }
        startCropBitmap.similarity(fillBitmap).also { similarity ->
            assertTrue(
                actual = similarity >= 10,
                message = "similarity = $similarity"
            )
        }
        centerCropBitmap.similarity(endCropBitmap).also { similarity ->
            assertTrue(
                actual = similarity >= 10,
                message = "similarity = $similarity"
            )
        }
        centerCropBitmap.similarity(fillBitmap).also { similarity ->
            assertTrue(
                actual = similarity in 5..10,
                message = "similarity = $similarity"
            )
        }
        endCropBitmap.similarity(fillBitmap).also { similarity ->
            assertTrue(
                actual = similarity >= 10,
                message = "similarity = $similarity"
            )
        }
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
        val bitmapDecoder = BitmapFactoryDecoder(
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
        BitmapFactoryDecoder(
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
        val element1 = BitmapFactoryDecoder(requestContext, dataSource)
        val element11 = BitmapFactoryDecoder(requestContext, dataSource)

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
        val decoder = BitmapFactoryDecoder(requestContext, dataSource)
        assertTrue(
            actual = decoder.toString().contains("BitmapFactoryDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        BitmapFactoryDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "BitmapFactoryDecoder",
            actual = BitmapFactoryDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = BitmapFactoryDecoder.Factory()

        ResourceImages.statics.plus(ResourceImages.anims)
            .forEach { imageFile ->
                ImageRequest(context, imageFile.uri)
                    .createDecoderOrNull(sketch, factory) {
                        it.copy(mimeType = it.mimeType)
                    }.apply {
                        assertTrue(this is BitmapFactoryDecoder)
                    }
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = BitmapFactoryDecoder.Factory()
        val element11 = BitmapFactoryDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "BitmapFactoryDecoder",
            actual = BitmapFactoryDecoder.Factory().toString()
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

        @WorkerThread
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