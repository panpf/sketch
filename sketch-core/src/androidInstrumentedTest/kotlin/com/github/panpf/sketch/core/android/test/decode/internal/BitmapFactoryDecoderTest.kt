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
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toDecoder
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
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapFactoryDecoderTest {

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = BitmapFactoryDecoder.Factory()

        ResourceImages.statics.forEach { imageFile ->
            try {
                ImageRequest(context, imageFile.uri)
                    .toDecoder(sketch, factory)
                    .imageInfo.apply {
                        assertSizeEquals(imageFile.size, this.size, delta = Size(1, 1))
                        assertEquals(imageFile.mimeType, this.mimeType)
                    }
            } catch (e: ImageInvalidException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun testDefault() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                bitmap.toShortInfoString()
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpace("SRGB")})",
                bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
        }

        // exif
        ResourceImages.clockExifs.forEach { imageFile ->
            ImageRequest(context, imageFile.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
            }.decode(sketch).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(
                    "Bitmap(1500x750,ARGB_8888${shortInfoColorSpace("SRGB")})",
                    bitmap.toShortInfoString()
                )
                assertEquals(
                    "ImageInfo(1500x750,'image/jpeg')",
                    imageInfo.toShortString()
                )
                assertEquals(LOCAL, dataFrom)
                assertNull(transformeds)
            }
        }
    }

    @Test
    fun testColorType() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,RGB_565${shortInfoColorSpace("SRGB")})",
                bitmap.toShortInfoString()
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1080x1344,RGB_565${shortInfoColorSpace("SRGB")})",
                bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
        }
    }

    @Test
    fun testColorSpace() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.O) return@runTest

        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                bitmap.toShortInfoString()
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
            assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpace("SRGB")})",
                bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
            assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ADOBE_RGB)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("ADOBE_RGB")})",
                bitmap.toShortInfoString()
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
            assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }

        ImageRequest(context, ResourceImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ADOBE_RGB)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpace("ADOBE_RGB")})",
                bitmap.toShortInfoString()
            )
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
            assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }
    }

    @Test
    fun testResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // precision = LESS_PIXELS
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(646, 968),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(323, 484),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            assertSizeEquals(
                expected = Size(322, 193),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            assertSizeEquals(
                expected = Size(290, 484),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(500, 300),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(300, 500),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
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
        assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        assertNotEquals(
            startCropBitmap.corners().toString(),
            centerCropBitmap.corners().toString()
        )
        assertNotEquals(
            startCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        assertNotEquals(
            startCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        assertNotEquals(
            centerCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        assertNotEquals(
            centerCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        assertNotEquals(endCropBitmap.corners().toString(), fillBitmap.corners().toString())
    }

    @Test
    fun testResizeNoRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // precision = LESS_PIXELS
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(350, 506),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(200, 200)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 200 * 200 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(87, 126),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            assertSizeEquals(
                expected = Size(175, 105),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            assertSizeEquals(
                expected = Size(152, 253),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(500, 300),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, ResourceImages.bmp.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(300, 500),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
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
        assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        assertNotEquals(
            startCropBitmap.corners().toString(),
            centerCropBitmap.corners().toString()
        )
        assertNotEquals(
            startCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        assertNotEquals(
            startCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        assertNotEquals(
            centerCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        assertNotEquals(
            centerCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        assertNotEquals(endCropBitmap.corners().toString(), fillBitmap.corners().toString())
    }

    @Test
    fun testResizeExif() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val testFile = ResourceImages.clockExifTranspose

        // precision = LESS_PIXELS
        ImageRequest(context, testFile.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(750, 375),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            assertSizeEquals(
                expected = Size(375, 188),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            assertSizeEquals(
                expected = Size(313, 188),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            assertSizeEquals(
                expected = Size(225, 375),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, testFile.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(500, 300),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
        }
        ImageRequest(context, testFile.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(300, 500),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNotNull(transformeds?.getSubsamplingTransformed())
            assertNotNull(transformeds?.getResizeTransformed())
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
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertSizeEquals(
                expected = Size(375, 188),
                actual = bitmap.size,
                delta = Size(1, 1)
            )
            assertEquals(
                "ImageInfo(1500x750,'image/jpeg')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNotNull(transformeds?.getInSampledTransformed())
            assertNull(transformeds?.getSubsamplingTransformed())
            assertNull(transformeds?.getResizeTransformed())
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
        assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        assertEquals(
            expected = 10,
            actual = startCropBitmap.similarity(centerCropBitmap),
        )
        assertEquals(
            expected = 12,
            actual = startCropBitmap.similarity(endCropBitmap)
        )
        assertEquals(
            expected = 11,
            actual = startCropBitmap.similarity(fillBitmap)
        )
        assertEquals(
            expected = 15,
            actual = centerCropBitmap.similarity(endCropBitmap)
        )
        assertEquals(
            expected = 8,
            actual = centerCropBitmap.similarity(fillBitmap)
        )
        assertEquals(
            expected = 13,
            actual = endCropBitmap.similarity(fillBitmap)
        )
    }

    @Test
    fun testError() = runTest {
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
    fun testFactoryEqualsAndHashCode() {
        val element1 = BitmapFactoryDecoder.Factory()
        val element11 = BitmapFactoryDecoder.Factory()
        val element2 = BitmapFactoryDecoder.Factory()

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertEquals(element1, element2)
        assertEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertEquals(element1.hashCode(), element2.hashCode())
        assertEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryKeyAndToString() {
        BitmapFactoryDecoder.Factory().apply {
            assertEquals("BitmapFactoryDecoder", key)
            assertEquals("BitmapFactoryDecoder", toString())
        }
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