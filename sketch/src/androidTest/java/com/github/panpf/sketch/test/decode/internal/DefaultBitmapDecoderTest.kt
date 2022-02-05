package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.graphics.ColorSpace.Named.SRGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.util.Size
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
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.jpeg")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1291, height=1936, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
        }

        LoadRequest(newAssetUri("sample.webp")).run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.webp")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1080, height=1344, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1080, height=1344, mimeType='image/webp'), " +
                        "exifOrientation=UNDEFINED, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
        }

        // exif
        ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg").files()
            .forEach { testFile ->
                LoadRequest(testFile.file.path).run {
                    DefaultBitmapDecoder(
                        sketch, this, FileDataSource(sketch, this, testFile.file)
                    ).use {
                        runBlocking { it.decode() }
                    }
                }.apply {
                    val exifOrientationHelper = ExifOrientationHelper(testFile.exifOrientation)
                    val addedSize = exifOrientationHelper.addToSize(Size(1500, 750))
                    Assert.assertEquals(
                        "BitmapDecodeResult(" +
                                "bitmap=Bitmap(width=${addedSize.width}, height=${addedSize.height}, config=ARGB_8888), " +
                                "imageInfo=ImageInfo(width=${addedSize.width}, height=${addedSize.height}, mimeType='image/jpeg'), " +
                                "exifOrientation=${exifOrientationName(testFile.exifOrientation)}, " +
                                "dataFrom=LOCAL, " +
                                "transformedList=null" +
                                ")",
                        this.toString()
                    )
                }
            }
    }

    @Test
    fun testMaxSize() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        LoadRequest(newAssetUri("sample.jpeg")) {
            maxSize(800, 800)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .use { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=646, height=968, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=[InSampledTransformed(2)]" +
                        ")",
                this.toString()
            )
        }

        LoadRequest(newAssetUri("sample.webp")) {
            maxSize(800, 800)
        }.run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.webp")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=540, height=672, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1080, height=1344, mimeType='image/webp'), " +
                        "exifOrientation=UNDEFINED, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=[InSampledTransformed(2)]" +
                        ")",
                this.toString()
            )
        }

        // exif
        ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg").files()
            .forEach { testFile ->
                LoadRequest(testFile.file.path) {
                    maxSize(800, 800)
                }.run {
                    DefaultBitmapDecoder(
                        sketch, this, FileDataSource(sketch, this, testFile.file)
                    ).use {
                        runBlocking { it.decode() }
                    }
                }.apply {
                    val exifOrientationHelper = ExifOrientationHelper(testFile.exifOrientation)
                    val addedSize = exifOrientationHelper.addToSize(Size(1500, 750))
                    Assert.assertEquals(
                        "BitmapDecodeResult(" +
                                "bitmap=Bitmap(width=${addedSize.width / 2}, height=${addedSize.height / 2}, config=ARGB_8888), " +
                                "imageInfo=ImageInfo(width=${addedSize.width}, height=${addedSize.height}, mimeType='image/jpeg'), " +
                                "exifOrientation=${exifOrientationName(testFile.exifOrientation)}, " +
                                "dataFrom=LOCAL, " +
                                "transformedList=[InSampledTransformed(2)]" +
                                ")",
                        this.toString()
                    )
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
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.jpeg")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1291, height=1936, config=RGB_565), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
        }

        LoadRequest(newAssetUri("sample.webp")) {
            bitmapConfig(RGB_565)
        }.run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.webp")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1080, height=1344, config=RGB_565), " +
                        "imageInfo=ImageInfo(width=1080, height=1344, mimeType='image/webp'), " +
                        "exifOrientation=UNDEFINED, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        LoadRequest(newAssetUri("sample.jpeg")).run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.jpeg")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1291, height=1936, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        LoadRequest(newAssetUri("sample.webp")).run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.webp")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1080, height=1344, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1080, height=1344, mimeType='image/webp'), " +
                        "exifOrientation=UNDEFINED, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
            Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
        }

        LoadRequest(newAssetUri("sample.jpeg")) {
            colorSpace(ColorSpace.get(ADOBE_RGB))
        }.run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.jpeg")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1291, height=1936, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")", this.toString()
            )
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }

        LoadRequest(newAssetUri("sample.webp")) {
            colorSpace(ColorSpace.get(ADOBE_RGB))
        }.run {
            DefaultBitmapDecoder(
                sketch, this, AssetDataSource(sketch, this, "sample.webp")
            ).use {
                runBlocking { it.decode() }
            }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=1080, height=1344, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1080, height=1344, mimeType='image/webp'), " +
                        "exifOrientation=UNDEFINED, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=null" +
                        ")",
                this.toString()
            )
            Assert.assertEquals(ColorSpace.get(ADOBE_RGB), bitmap.colorSpace)
        }
    }

    @Test
    fun testResize() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        /*
         * resize region
         */
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300)
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .use { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=322, height=193, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=[ResizeTransformed(Resize(500x300,All,CENTER_CROP,KEEP_ASPECT_RATIO)), InSampledTransformed(4)]" +
                        ")",
                this.toString()
            )
        }

        // scope
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(500, 300, scope = Resize.Scope.OnlyLongImage())
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .use { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=322, height=193, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=[ResizeTransformed(Resize(500x300,OnlyLongImage(2.0),CENTER_CROP,KEEP_ASPECT_RATIO)), InSampledTransformed(4)]" +
                        ")",
                this.toString()
            )
        }
        LoadRequest(newAssetUri("sample.jpeg")) {
            resize(300, 500, scope = Resize.Scope.OnlyLongImage())
        }.run {
            DefaultBitmapDecoder(sketch, this, AssetDataSource(sketch, this, "sample.jpeg"))
                .use { runBlocking { it.decode() } }
        }.apply {
            Assert.assertEquals(
                "BitmapDecodeResult(" +
                        "bitmap=Bitmap(width=323, height=484, config=ARGB_8888), " +
                        "imageInfo=ImageInfo(width=1291, height=1936, mimeType='image/jpeg'), " +
                        "exifOrientation=NORMAL, " +
                        "dataFrom=LOCAL, " +
                        "transformedList=[InSampledTransformed(4)]" +
                        ")",
                this.toString()
            )
        }

//        // resize region unusable
//        val request5 = LoadRequest(newAssetUri("sample.bmp")) {
//            resize(500, 500)
//        }
//        DefaultBitmapDecoder(sketch, request5, AssetDataSource(sketch, request5, "sample.bmp"))
//            .use { runBlocking { it.decode() } }
//            .apply {
//                Assert.assertEquals(350, bitmap.width)
//                Assert.assertEquals(506, bitmap.height)
//                Assert.assertEquals(ARGB_8888, bitmap.config)
//                if (VERSION.SDK_INT >= VERSION_CODES.O) {
//                    Assert.assertEquals(ColorSpace.get(SRGB), bitmap.colorSpace)
//                }
//                Assert.assertEquals(700, imageInfo.width)
//                Assert.assertEquals(1012, imageInfo.height)
//                Assert.assertEquals("image/bmp", imageInfo.mimeType)
//                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
//                Assert.assertEquals(DataFrom.LOCAL, dataFrom)
//                Assert.assertEquals("[InSampledTransformed(2)]", transformedList.toString())
//            }
//        // todo exif
        TODO("")
    }

    @Test
    fun testDisabledBitmapPool() {
        // todo exif
        TODO("")
    }

    @Test
    fun testIgnoreExifOrientation() {
        // todo exif
        TODO("")

    }
}