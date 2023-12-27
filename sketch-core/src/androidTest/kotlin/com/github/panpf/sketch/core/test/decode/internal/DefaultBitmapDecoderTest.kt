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
package com.github.panpf.sketch.core.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.SRGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.datasource.BasedFileDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.BitmapDecodeException
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.tools4j.test.ktx.assertNoThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class DefaultBitmapDecoderTest {

    @Test
    fun testDefault() {
        val (context, sketch) = getTestContextAndNewSketch()

        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, AssetImages.webp.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp',UNDEFINED)",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'',UNDEFINED)", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        // exif
        ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.clockHor.fileName
        ).files()
            .forEach { testFile ->
                LoadRequest(context, testFile.file.path) {
                    resizeSize(3000, 3000)
                    resizePrecision(LESS_PIXELS)
                }.decode(sketch).apply {
                    Assert.assertEquals("Bitmap(1500x750,ARGB_8888)", bitmap.toShortInfoString())
                    Assert.assertEquals(
                        "ImageInfo(1500x750,'image/jpeg',${exifOrientationName(testFile.exifOrientation)})",
                        imageInfo.toShortString()
                    )
                    Assert.assertEquals(LOCAL, dataFrom)
                    Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
                }
            }
    }

    @Test
    fun testBitmapConfig() {
        val (context, sketch) = getTestContextAndNewSketch()

        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
            bitmapConfig(RGB_565)
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1291x1936,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, AssetImages.webp.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
            bitmapConfig(RGB_565)
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1080x1344,RGB_565)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp',UNDEFINED)",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'',UNDEFINED)", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val (context, sketch) = getTestContextAndNewSketch()

        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        LoadRequest(context, AssetImages.webp.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp',UNDEFINED)",
                    imageInfo.toShortString()
                )
            } else {
                Assert.assertEquals("ImageInfo(1080x1344,'')", imageInfo.toShortString())
            }
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
            colorSpace(ColorSpace.get(ADOBE_RGB))
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }

        LoadRequest(context, AssetImages.webp.uri) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
            colorSpace(ColorSpace.get(ADOBE_RGB))
        }.decode(sketch).apply {
            Assert.assertEquals("Bitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                Assert.assertEquals(
                    "ImageInfo(1080x1344,'image/webp',UNDEFINED)",
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
        val (context, sketch) = getTestContextAndNewSketch()

        // precision = LESS_PIXELS
        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(800, 800)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(646x968,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(322x193,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(300, 500)
            resizePrecision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(290x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 300)
            resizePrecision(EXACTLY)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(300, 500)
            resizePrecision(EXACTLY)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',NORMAL)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(START_CROP)
        }.decode(sketch).bitmap
        val centerCropBitmap = LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(CENTER_CROP)
        }.decode(sketch).bitmap
        val endCropBitmap = LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(END_CROP)
        }.decode(sketch).bitmap
        val fillBitmap = LoadRequest(context, AssetImages.jpeg.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(FILL)
        }.decode(sketch).bitmap
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
        val (context, sketch) = getTestContextAndNewSketch()

        // precision = LESS_PIXELS
        LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(350x506,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(200, 200)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 200 * 200 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(87x126,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(175x105,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(300, 500)
            resizePrecision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(152x253,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }

        // precision = EXACTLY
        LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 300)
            resizePrecision(EXACTLY)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(300, 500)
            resizePrecision(EXACTLY)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(700x1012,'image/bmp',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(START_CROP)
        }.decode(sketch).bitmap
        val centerCropBitmap = LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(CENTER_CROP)
        }.decode(sketch).bitmap
        val endCropBitmap = LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(END_CROP)
        }.decode(sketch).bitmap
        val fillBitmap = LoadRequest(context, AssetImages.bmp.uri) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(FILL)
        }.decode(sketch).bitmap
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
        val (context, sketch) = getTestContextAndNewSketch()

        val testFile = ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.jpeg.fileName
        ).files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        LoadRequest(context, testFile.file.path) {
            resizeSize(800, 800)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(646x968,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, testFile.file.path) {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(322x193,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, testFile.file.path) {
            resizeSize(300, 500)
            resizePrecision(SAME_ASPECT_RATIO)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(290x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(EXACTLY)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, testFile.file.path) {
            resizeSize(300, 500)
            resizePrecision(EXACTLY)
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = LongImageClipPrecisionDecider
        LoadRequest(context, testFile.file.path) {
            resizeSize(300, 400)
            resizePrecision(
                LongImageClipPrecisionDecider(
                    longImageDecider = DefaultLongImageDecider(
                        sameDirectionMultiple = 1f,
                        notSameDirectionMultiple = 5f
                    )
                )
            )
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(161x215,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1291x1936,'image/jpeg',TRANSPOSE)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(START_CROP)
        }.decode(sketch).bitmap
        val centerCropBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(CENTER_CROP)
        }.decode(sketch).bitmap
        val endCropBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(END_CROP)
        }.decode(sketch).bitmap
        val fillBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(FILL)
        }.decode(sketch).bitmap
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
        val (context, sketch) = getTestContextAndNewSketch()

        val testFile = ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.jpeg.fileName
        ).files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        LoadRequest(context, testFile.file.path) {
            resizeSize(800, 800)
            resizePrecision(LESS_PIXELS)
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(968x646,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, testFile.file.path) {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(484x323,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = SAME_ASPECT_RATIO
        LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(484x290,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, testFile.file.path) {
            resizeSize(300, 500)
            resizePrecision(SAME_ASPECT_RATIO)
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(193x322,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(EXACTLY)
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(context, testFile.file.path) {
            resizeSize(300, 500)
            resizePrecision(EXACTLY)
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = LongImageClipPrecisionDecider
        LoadRequest(context, testFile.file.path) {
            resizeSize(300, 400)
            resizePrecision(
                LongImageClipPrecisionDecider(
                    longImageDecider = DefaultLongImageDecider(
                        sameDirectionMultiple = 1f,
                        notSameDirectionMultiple = 5f
                    )
                )
            )
            ignoreExifOrientation()
        }.decode(sketch).apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(242x162,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(1936x1291,'image/jpeg',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getSubsamplingTransformed())
            Assert.assertNull(transformedList?.getExifOrientationTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(START_CROP)
            ignoreExifOrientation()
        }.decode(sketch).bitmap
        val centerCropBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(CENTER_CROP)
            ignoreExifOrientation()
        }.decode(sketch).bitmap
        val endCropBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(END_CROP)
            ignoreExifOrientation()
        }.decode(sketch).bitmap
        val fillBitmap = LoadRequest(context, testFile.file.path) {
            resizeSize(500, 300)
            resizePrecision(SAME_ASPECT_RATIO)
            resizeScale(FILL)
            ignoreExifOrientation()
        }.decode(sketch).bitmap
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
        val element1 = DefaultBitmapDecoder.Factory()
        val element11 = DefaultBitmapDecoder.Factory()
        val element2 = DefaultBitmapDecoder.Factory()

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
    fun testError() {
        val (context, sketch) = getTestContextAndNewSketch {
            bitmapPool(LruBitmapPool(1024 * 1024 * 20))
        }

        /* full */
        assertThrow(BitmapDecodeException::class) {
            val request = LoadRequest(context, AssetImages.jpeg.uri)
            val dataSource = runBlocking {
                sketch.components.newFetcherOrThrow(request).fetch()
            }.getOrThrow().dataSource
            sketch.bitmapPool.put(Bitmap.createBitmap(1291, 1936, ARGB_8888))
            DefaultBitmapDecoder(
                sketch, request.toRequestContext(), FullTestDataSource(dataSource.asOrThrow())
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        assertNoThrow {
            val request = LoadRequest(context, AssetImages.jpeg.uri)
            val dataSource = runBlocking {
                sketch.components.newFetcherOrThrow(request).fetch()
            }.getOrThrow().dataSource
            sketch.bitmapPool.put(Bitmap.createBitmap(1291, 1936, ARGB_8888))
            DefaultBitmapDecoder(
                sketch,
                request.toRequestContext(),
                FullTestDataSource(dataSource.asOrThrow(), enabledCount = true)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        /* region */
        assertThrow(BitmapDecodeException::class) {
            val request1 = LoadRequest(context, AssetImages.jpeg.uri) {
                resizeSize(500, 500)
                resizePrecision(EXACTLY)
            }
            val dataSource1 = runBlocking {
                sketch.components.newFetcherOrThrow(request1).fetch()
            }.getOrThrow().dataSource
            DefaultBitmapDecoder(
                sketch,
                request1.toRequestContext(),
                RegionTestDataSource(dataSource1.asOrThrow(), true)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        assertThrow(BitmapDecodeException::class) {
            val request1 = LoadRequest(context, AssetImages.jpeg.uri) {
                resizeSize(500, 500)
                resizePrecision(EXACTLY)
            }
            val dataSource1 = runBlocking {
                sketch.components.newFetcherOrThrow(request1).fetch()
            }.getOrThrow().dataSource
            DefaultBitmapDecoder(
                sketch,
                request1.toRequestContext(),
                RegionTestDataSource(dataSource1.asOrThrow(), false)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        assertNoThrow {
            val request1 = LoadRequest(context, AssetImages.jpeg.uri) {
                resizeSize(500, 500)
                resizePrecision(EXACTLY)
            }
            val dataSource1 = runBlocking {
                sketch.components.newFetcherOrThrow(request1).fetch()
            }.getOrThrow().dataSource
            DefaultBitmapDecoder(
                sketch,
                request1.toRequestContext(),
                RegionTestDataSource(dataSource1.asOrThrow(), false, enabledCount = true)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }

        assertThrow(BitmapDecodeException::class) {
            val request1 = LoadRequest(context, AssetImages.jpeg.uri) {
                resizeSize(500, 500)
                resizePrecision(EXACTLY)
            }
            val dataSource1 = runBlocking {
                sketch.components.newFetcherOrThrow(request1).fetch()
            }.getOrThrow().dataSource
            DefaultBitmapDecoder(
                sketch,
                request1.toRequestContext(),
                RegionTestDataSource(dataSource1.asOrThrow(), null)
            ).let { runBlocking { it.decode() } }.getOrThrow()
        }
    }

    class FullTestDataSource(
        private val fileDataSource: BasedFileDataSource,
        private val enabledCount: Boolean = false,
    ) : BasedFileDataSource by fileDataSource {

        private var count = 0

        @WorkerThread
        override fun newInputStream(): InputStream {
            val stackStringList = Exception().stackTraceToString().split("\n")
            if (stackStringList.find { it.contains(".realDecodeFull(") } != null) {
                count++
                if (!enabledCount || count == 1) {
                    throw IllegalArgumentException("Problem decoding into existing bitmap")
                }
            }
            return fileDataSource.newInputStream()
        }
    }

    class RegionTestDataSource(
        private val fileDataSource: BasedFileDataSource,
        private val srcError: Boolean? = false,
        private val enabledCount: Boolean = false,
    ) : BasedFileDataSource by fileDataSource {

        private var count = 0

        @WorkerThread
        override fun newInputStream(): InputStream {
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
            return fileDataSource.newInputStream()
        }
    }
}