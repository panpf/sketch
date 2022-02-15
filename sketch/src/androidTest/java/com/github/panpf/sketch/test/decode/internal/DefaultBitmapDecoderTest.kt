package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.SRGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.resize.Precision.EXACTLY
import com.github.panpf.sketch.decode.resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.decode.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.decode.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.decode.resize.Scale.END_CROP
import com.github.panpf.sketch.decode.resize.Scale.FILL
import com.github.panpf.sketch.decode.resize.Scale.START_CROP
import com.github.panpf.sketch.decode.resize.getResizeTransformed
import com.github.panpf.sketch.decode.resize.longImageClipPrecision
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultBitmapDecoderTest {

    @Test
    fun testDefault() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        LoadRequest(newAssetUri("sample.jpeg")).run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(newAssetUri("sample.webp")).run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.webp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1080x1344,'image/webp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        // exif
        ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg").files().forEach {
            LoadRequest(it.file.path).run {
                DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, it.file))
                    .let { runBlocking { it.decode() } }
            }.apply {
                Assert.assertEquals("Bitmap(1500x750,ARGB_8888)", bitmap.toShortInfoString())
                Assert.assertEquals(
                    "ImageInfo(1500x750,'image/jpeg')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(it.exifOrientation, exifOrientation)
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertNotNull(transformedList?.getExifOrientationTransformed())
            }
        }
    }

    @Test
    fun testBitmapConfig() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        LoadRequest(newAssetUri("sample.jpeg")) {
            bitmapConfig(RGB_565)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1291x1936,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(newAssetUri("sample.webp")) {
            bitmapConfig(RGB_565)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.webp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1080x1344,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1080x1344,'image/webp')", imageInfo.toShortString())
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertNull(transformedList)
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        LoadRequest(newAssetUri("sample.jpeg")).run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        LoadRequest(newAssetUri("sample.webp")).run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.webp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1080x1344,'image/webp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        LoadRequest(newAssetUri("sample.jpeg")) {
            colorSpace(ColorSpace.get(ADOBE_RGB))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1291x1936,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }

        LoadRequest(newAssetUri("sample.webp")) {
            colorSpace(ColorSpace.get(ADOBE_RGB))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.webp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals("Bitmap(1080x1344,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1080x1344,'image/webp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }
    }

    @Test
    fun testResize() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        // precision = LESS_PIXELS
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(800, 800, precision = LESS_PIXELS)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(646x968,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 500, precision = LESS_PIXELS)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }

        // precision = KEEP_ASPECT_RATIO
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(322x193,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(300, 500, precision = KEEP_ASPECT_RATIO)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(290x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, precision = EXACTLY)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(300, 500, precision = EXACTLY)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precisionDecider = LongImageClipPrecisionDecider
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 333, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 333 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(333).format(1)
            )
            Assert.assertEquals("Bitmap(322x214,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 357, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 357 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(357).format(1)
            )
            Assert.assertEquals("Bitmap(322x230,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 384, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 384 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = START_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val centerCropBitmap = LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = CENTER_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val endCropBitmap = LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = END_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val fillBitmap = LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = FILL)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .let { runBlocking { it.decode() } }
        }.bitmap
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
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        // precision = LESS_PIXELS
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 500, precision = LESS_PIXELS)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(350x506,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(200, 200, precision = LESS_PIXELS)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 200 * 200 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(87x126,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // precision = KEEP_ASPECT_RATIO
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(350x210,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(300, 500, precision = KEEP_ASPECT_RATIO)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }

        // precision = EXACTLY
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 300, precision = EXACTLY)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(300, 500, precision = EXACTLY)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precisionDecider = LongImageClipPrecisionDecider
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 353, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 353 * 1.1f
            )
            Assert.assertEquals("Bitmap(350x247,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 357, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 357 * 1.1f
            )
            Assert.assertEquals("Bitmap(350x250,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 384, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 384 * 1.1f
            )
            Assert.assertEquals("Bitmap(350x506,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(700x1012,'image/bmp')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = START_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val centerCropBitmap = LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = CENTER_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val endCropBitmap = LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = END_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val fillBitmap = LoadRequest(newAssetUri("sample.bmp")) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = FILL)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.bmp"))
                .let { runBlocking { it.decode() } }
        }.bitmap
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
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        val testFile = ExifOrientationTestFileHelper(context, "sample.jpeg").files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        LoadRequest(testFile.file.path) {
            resize(800, 800, precision = LESS_PIXELS)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(646x968,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(500, 500, precision = LESS_PIXELS)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }

        // precision = KEEP_ASPECT_RATIO
        LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(322x193,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(300, 500, precision = KEEP_ASPECT_RATIO)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(290x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        LoadRequest(testFile.file.path) {
            resize(500, 300, precision = EXACTLY)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(300, 500, precision = EXACTLY)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precisionDecider = LongImageClipPrecisionDecider
        LoadRequest(testFile.file.path) {
            resize(500, 333, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 333 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(333).format(1)
            )
            Assert.assertEquals("Bitmap(322x214,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(500, 357, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 357 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(357).format(1)
            )
            Assert.assertEquals("Bitmap(322x230,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(500, 384, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 384 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(323x484,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1291x1936,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_TRANSPOSE, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = START_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val centerCropBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = CENTER_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val endCropBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = END_CROP)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val fillBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = FILL)
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
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
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        val testFile = ExifOrientationTestFileHelper(context, "sample.jpeg").files()
            .find { it.exifOrientation == ExifInterface.ORIENTATION_TRANSPOSE }!!

        // precision = LESS_PIXELS
        LoadRequest(testFile.file.path) {
            resize(800, 800, precision = LESS_PIXELS)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 800 * 800 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(968x646,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(500, 500, precision = LESS_PIXELS)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(484x323,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
        }

        // precision = KEEP_ASPECT_RATIO
        LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                500f.div(300).format(1)
            )
            Assert.assertEquals("Bitmap(484x290,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(300, 500, precision = KEEP_ASPECT_RATIO)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                300f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(193x322,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precision = EXACTLY
        LoadRequest(testFile.file.path) {
            resize(500, 300, precision = EXACTLY)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 500 * 300 * 1.1f
            )
            Assert.assertEquals("Bitmap(500x300,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(300, 500, precision = EXACTLY)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 300 * 500 * 1.1f
            )
            Assert.assertEquals("Bitmap(300x500,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }

        // precisionDecider = LongImageClipPrecisionDecider
        LoadRequest(testFile.file.path) {
            resize(333, 500, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 333 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                333f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(214x322,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(357, 500, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 357 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                357f.div(500).format(1)
            )
            Assert.assertEquals("Bitmap(230x322,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNotNull(transformedList?.getResizeTransformed())
        }
        LoadRequest(testFile.file.path) {
            resize(384, 500, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.apply {
            Assert.assertTrue(
                "${bitmap.width}x${bitmap.height}",
                bitmap.width * bitmap.height <= 384 * 500 * 1.1f
            )
            Assert.assertEquals(
                bitmap.width.toFloat().div(bitmap.height).format(1),
                imageInfo.width.toFloat().div(imageInfo.height).format(1)
            )
            Assert.assertEquals("Bitmap(484x323,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals("ImageInfo(1936x1291,'image/jpeg')", imageInfo.toShortString())
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNotNull(transformedList?.getInSampledTransformed())
            Assert.assertNull(transformedList?.getResizeTransformed())
        }

        // scale
        val startCropBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = START_CROP)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val centerCropBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = CENTER_CROP)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val endCropBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = END_CROP)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
        val fillBitmap = LoadRequest(testFile.file.path) {
            resize(500, 300, precision = KEEP_ASPECT_RATIO, scale = FILL)
            ignoreExifOrientation()
        }.run {
            DefaultBitmapDecoder(sketch, this, FileDataSource(sketch, this, testFile.file))
                .let { runBlocking { it.decode() } }
        }.bitmap
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
}