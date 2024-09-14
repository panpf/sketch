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
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.shortInfoColorSpaceName
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
    fun testDefault() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
        ExifOrientationTestFileHelper(
            context,
            ResourceImages.clockHor.resourceName
        ).files()
            .forEach { testFile ->
                ImageRequest(context, testFile.file.path) {
                    size(3000, 3000)
                    precision(LESS_PIXELS)
                }.decode(sketch).apply {
                    val bitmap = image.getBitmapOrThrow()
                    assertEquals(
                        "Bitmap(1500x750,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
    fun testColorType() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorType(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,RGB_565${shortInfoColorSpaceName("SRGB")})",
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
                "Bitmap(1080x1344,RGB_565${shortInfoColorSpaceName("SRGB")})",
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
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("ADOBE_RGB")})",
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
                "Bitmap(1080x1344,ARGB_8888${shortInfoColorSpaceName("ADOBE_RGB")})",
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
    fun testResize() {
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
            assertEquals(
                "Bitmap(646x968,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(323x484,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(322x193,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(290x484,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(500x300,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(300x500,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
    fun testResizeNoRegion() {
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
            assertEquals(
                "Bitmap(350x506,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(87x126,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(175x105,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(152x253,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(500x300,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
            assertEquals(
                "Bitmap(300x500,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
    fun testResizeExif() {
        val (context, sketch) = getTestContextAndSketch()

        val testFile = ExifOrientationTestFileHelper(
            context,
            ResourceImages.jpeg.resourceName
        ).files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        ImageRequest(context, testFile.file.path) {
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
            assertEquals(
                "Bitmap(646x968,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
        ImageRequest(context, testFile.file.path) {
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
            assertEquals(
                "Bitmap(323x484,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
        ImageRequest(context, testFile.file.path) {
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
            assertEquals(
                "Bitmap(322x193,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
        ImageRequest(context, testFile.file.path) {
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
            assertEquals(
                "Bitmap(290x484,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
        ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                "Bitmap(500x300,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
        ImageRequest(context, testFile.file.path) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            assertTrue(
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f,
                "${bitmap.width}x${bitmap.height}"
            )
            assertEquals(
                "Bitmap(300x500,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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

        // precision = LongImagePrecisionDecider
        ImageRequest(context, testFile.file.path) {
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
            assertEquals(
                "Bitmap(161x215,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                bitmap.toShortInfoString()
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
        val startCropBitmap = ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, testFile.file.path) {
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