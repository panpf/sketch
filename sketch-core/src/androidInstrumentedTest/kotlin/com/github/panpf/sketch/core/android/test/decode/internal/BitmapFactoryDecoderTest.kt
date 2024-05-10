/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okio.Source
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFails

@RunWith(AndroidJUnit4::class)
class BitmapFactoryDecoderTest {

    @Test
    fun testDefault() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, MyImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, MyImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        // exif
        ExifOrientationTestFileHelper(
            context,
            MyImages.clockHor.fileName
        ).files()
            .forEach { testFile ->
                ImageRequest(context, testFile.file.path) {
                    size(3000, 3000)
                    precision(LESS_PIXELS)
                }.decode(sketch).apply {
                    val bitmap = image.getBitmapOrThrow()
                    Assert.assertEquals(
                        "AndroidBitmap(1500x750,ARGB_8888)",
                        bitmap.toShortInfoString()
                    )
                    Assert.assertEquals(
                        "ImageInfo(1500x750,'image/jpeg')",
                        imageInfo.toShortString()
                    )
                    Assert.assertEquals(LOCAL, dataFrom)
                    Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
                }
            }
    }

    @Test
    fun testBitmapConfig() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, MyImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            bitmapConfig(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1291x1936,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, MyImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            bitmapConfig(RGB_565)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1080x1344,RGB_565)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, MyImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        ImageRequest(context, MyImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        ImageRequest(context, MyImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ADOBE_RGB)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }

        ImageRequest(context, MyImages.webp.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
            colorSpace(ADOBE_RGB)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertEquals("AndroidBitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp')",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }
    }

    @Test
    fun testResize() {
        val (context, sketch) = getTestContextAndSketch()

        // precision = LESS_PIXELS
        ImageRequest(context, MyImages.jpeg.uri) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(646x968,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("AndroidBitmap(322x193,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, MyImages.jpeg.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("AndroidBitmap(290x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, MyImages.jpeg.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch).image.getBitmapOrThrow()
        Assert.assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            centerCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(endCropBitmap.corners().toString(), fillBitmap.corners().toString())
    }

    @Test
    fun testResizeNoRegion() {
        val (context, sketch) = getTestContextAndSketch()

        // precision = LESS_PIXELS
        ImageRequest(context, MyImages.bmp.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(350x506,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, MyImages.bmp.uri) {
            size(200, 200)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 200 * 200 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(87x126,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, MyImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("AndroidBitmap(175x105,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, MyImages.bmp.uri) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("AndroidBitmap(152x253,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, MyImages.bmp.uri) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, MyImages.bmp.uri) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = ImageRequest(context, MyImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val centerCropBitmap = ImageRequest(context, MyImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val endCropBitmap = ImageRequest(context, MyImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.decode(sketch).image.getBitmapOrThrow()
        val fillBitmap = ImageRequest(context, MyImages.bmp.uri) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.decode(sketch).image.getBitmapOrThrow()
        Assert.assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            centerCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(endCropBitmap.corners().toString(), fillBitmap.corners().toString())
    }

    @Test
    fun testResizeExif() {
        val (context, sketch) = getTestContextAndSketch()

        val testFile = ExifOrientationTestFileHelper(
            context,
            MyImages.jpeg.fileName
        ).files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        ImageRequest(context, testFile.file.path) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(646x968,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, testFile.file.path) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("AndroidBitmap(322x193,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, testFile.file.path) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("AndroidBitmap(290x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, testFile.file.path) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = LongImageClipPrecisionDecider
        ImageRequest(context, testFile.file.path) {
            size(300, 400)
            precision(
                LongImageClipPrecisionDecider(
                    longImageDecider = DefaultLongImageDecider(
                        sameDirectionMultiple = 1f,
                        notSameDirectionMultiple = 5f
                    )
                )
            )
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(161x215,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
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
        Assert.assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            centerCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(endCropBitmap.corners().toString(), fillBitmap.corners().toString())
    }

    @Test
    fun testResizeExifIgnore() {
        val (context, sketch) = getTestContextAndSketch()

        val testFile = ExifOrientationTestFileHelper(
            context,
            MyImages.jpeg.fileName
        ).files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        ImageRequest(context, testFile.file.path) {
            size(800, 800)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(968x646,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, testFile.file.path) {
            size(500, 500)
            precision(LESS_PIXELS)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("AndroidBitmap(484x323,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("AndroidBitmap(484x290,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, testFile.file.path) {
            size(300, 500)
            precision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("AndroidBitmap(193x322,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        ImageRequest(context, testFile.file.path) {
            size(500, 300)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        ImageRequest(context, testFile.file.path) {
            size(300, 500)
            precision(EXACTLY)
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = LongImageClipPrecisionDecider
        ImageRequest(context, testFile.file.path) {
            size(300, 400)
            precision(
                LongImageClipPrecisionDecider(
                    longImageDecider = DefaultLongImageDecider(
                        sameDirectionMultiple = 1f,
                        notSameDirectionMultiple = 5f
                    )
                )
            )
        }.decode(sketch).apply {
            val bitmap = image.getBitmapOrThrow()
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("AndroidBitmap(242x162,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
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
        Assert.assertTrue(startCropBitmap.width * startCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(centerCropBitmap.width * centerCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(endCropBitmap.width * endCropBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertTrue(fillBitmap.width * fillBitmap.height <= 500 * 300 * 1.1f)
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            centerCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            startCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            endCropBitmap.corners().toString()
        )
        Assert.assertNotEquals(
            centerCropBitmap.corners().toString(),
            fillBitmap.corners().toString()
        )
        Assert.assertNotEquals(endCropBitmap.corners().toString(), fillBitmap.corners().toString())
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = BitmapFactoryDecoder.Factory()
        val element11 = BitmapFactoryDecoder.Factory()
        val element2 = BitmapFactoryDecoder.Factory()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element2)
        Assert.assertEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element2.hashCode())
        Assert.assertEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testError() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* full */
        assertFails {
            val request = ImageRequest(context, MyImages.jpeg.uri) {
                resize(MyImages.jpeg.size.width * 2, MyImages.jpeg.size.height * 2)
            }
            val dataSource = runBlocking {
                sketch.components.newFetcherOrThrow(request).fetch()
            }.getOrThrow().dataSource
            val bitmapDecoder = BitmapFactoryDecoder(
                requestContext = request.toRequestContext(sketch),
                dataSource = FullTestDataSource(dataSource.asOrThrow())
            )
            val result = runBlocking { bitmapDecoder.decode() }
            result.getOrThrow()
        }

        val request = ImageRequest(context, MyImages.jpeg.uri) {
            resize(MyImages.jpeg.size.width * 2, MyImages.jpeg.size.height * 2)
        }
        val dataSource = runBlocking {
            sketch.components.newFetcherOrThrow(request).fetch()
        }.getOrThrow().dataSource
        val bitmapDecoder = BitmapFactoryDecoder(
            requestContext = request.toRequestContext(sketch),
            dataSource = FullTestDataSource(dataSource.asOrThrow(), enabledCount = true)
        )
        val result = runBlocking { bitmapDecoder.decode() }
        result.exceptionOrNull()?.printStackTrace()
        result.getOrThrow()

        /* region */
        assertFails {
            val request1 = ImageRequest(context, MyImages.jpeg.uri) {
                size(500, 500)
                precision(EXACTLY)
            }
            val dataSource1 = runBlocking {
                sketch.components.newFetcherOrThrow(request1).fetch()
            }.getOrThrow().dataSource
            BitmapFactoryDecoder(
                request1.toRequestContext(sketch),
                RegionTestDataSource(dataSource1.asOrThrow(), true)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        assertFails {
            val request1 = ImageRequest(context, MyImages.jpeg.uri) {
                size(500, 500)
                precision(EXACTLY)
            }
            val dataSource1 = runBlocking {
                sketch.components.newFetcherOrThrow(request1).fetch()
            }.getOrThrow().dataSource
            BitmapFactoryDecoder(
                request1.toRequestContext(sketch),
                RegionTestDataSource(dataSource1.asOrThrow(), false)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        val request1 = ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 500)
            precision(EXACTLY)
        }
        val dataSource1 = runBlocking {
            sketch.components.newFetcherOrThrow(request1).fetch()
        }.getOrThrow().dataSource
        BitmapFactoryDecoder(
            request1.toRequestContext(sketch),
            RegionTestDataSource(dataSource1.asOrThrow(), false, enabledCount = true)
        ).let { runBlocking { it.decode() } }.getOrThrow()

        assertFails {
            val request2 = ImageRequest(context, MyImages.jpeg.uri) {
                size(500, 500)
                precision(EXACTLY)
            }
            val dataSource2 = runBlocking {
                sketch.components.newFetcherOrThrow(request2).fetch()
            }.getOrThrow().dataSource
            BitmapFactoryDecoder(
                request2.toRequestContext(sketch),
                RegionTestDataSource(dataSource2.asOrThrow(), null)
            ).let { runBlocking { it.decode() } }.getOrThrow()
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