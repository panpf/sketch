package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.applyResize
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampleSizeWithTolerance
import com.github.panpf.sketch.decode.internal.calculateSamplingSize
import com.github.panpf.sketch.decode.internal.calculateSamplingSizeForRegion
import com.github.panpf.sketch.decode.internal.computeSizeMultiplier
import com.github.panpf.sketch.decode.internal.decodeBitmapWithBitmapFactory
import com.github.panpf.sketch.decode.internal.decodeRegionBitmap
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.limitedMaxBitmapSize
import com.github.panpf.sketch.decode.internal.maxBitmapSize
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactory
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.decode.internal.sizeString
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.test.contextAndSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DecodeUtilsTest {

    @Test
    fun testLimitedMaxBitmapSize() {
        val maxSize = maxBitmapSize.width
        Assert.assertEquals(1, limitedMaxBitmapSize(maxSize - 1, maxSize, 1))
        Assert.assertEquals(1, limitedMaxBitmapSize(maxSize, maxSize - 1, 1))
        Assert.assertEquals(1, limitedMaxBitmapSize(maxSize - 1, maxSize - 1, 1))
        Assert.assertEquals(1, limitedMaxBitmapSize(maxSize, maxSize, 1))
        Assert.assertEquals(2, limitedMaxBitmapSize(maxSize + 1, maxSize, 1))
        Assert.assertEquals(2, limitedMaxBitmapSize(maxSize, maxSize + 1, 1))
        Assert.assertEquals(2, limitedMaxBitmapSize(maxSize + 1, maxSize + 1, 1))

        Assert.assertEquals(1, limitedMaxBitmapSize(maxSize, maxSize, 0))
        Assert.assertEquals(1, limitedMaxBitmapSize(maxSize, maxSize, -1))
        Assert.assertEquals(2, limitedMaxBitmapSize(maxSize + 1, maxSize + 1, -1))
        Assert.assertEquals(2, limitedMaxBitmapSize(maxSize + 1, maxSize + 1, 0))
    }

    @Test
    fun testCalculateSampleSize() {
        Assert.assertEquals(1, calculateSampleSize(1000, 1000, 1100, 1100))
        Assert.assertEquals(1, calculateSampleSize(1000, 1000, 1000, 1000))
        Assert.assertEquals(2, calculateSampleSize(1000, 1000, 900, 900))

        Assert.assertEquals(2, calculateSampleSize(1000, 1000, 520, 520))
        Assert.assertEquals(2, calculateSampleSize(1000, 1000, 500, 500))
        Assert.assertEquals(4, calculateSampleSize(1000, 1000, 480, 480))

        Assert.assertEquals(4, calculateSampleSize(1000, 1000, 260, 260))
        Assert.assertEquals(4, calculateSampleSize(1000, 1000, 250, 250))
        Assert.assertEquals(8, calculateSampleSize(1000, 1000, 240, 240))
    }

    @Test
    fun testCalculateSampleSizeWithTolerance() {
        Assert.assertEquals(1, calculateSampleSizeWithTolerance(1000, 1000, 1020, 1020))
        Assert.assertEquals(1, calculateSampleSizeWithTolerance(1000, 1000, 1000, 1000))
        Assert.assertEquals(1, calculateSampleSizeWithTolerance(1000, 1000, 980, 980))

        Assert.assertEquals(2, calculateSampleSizeWithTolerance(1000, 1000, 520, 520))
        Assert.assertEquals(2, calculateSampleSizeWithTolerance(1000, 1000, 500, 500))
        Assert.assertEquals(2, calculateSampleSizeWithTolerance(1000, 1000, 480, 480))

        Assert.assertEquals(4, calculateSampleSizeWithTolerance(1000, 1000, 260, 260))
        Assert.assertEquals(4, calculateSampleSizeWithTolerance(1000, 1000, 250, 250))
        Assert.assertEquals(4, calculateSampleSizeWithTolerance(1000, 1000, 240, 240))
    }

    @Test
    fun testCalculateSamplingSize() {
        Assert.assertEquals(75, calculateSamplingSize(150, 2))
        Assert.assertEquals(76, calculateSamplingSize(151, 2))
    }

    @Test
    fun testCalculateSamplingSizeForRegion() {
        Assert.assertEquals(75, calculateSamplingSizeForRegion(150, 2))
        Assert.assertEquals(75, calculateSamplingSizeForRegion(151, 2))
    }

    @Test
    fun testRealDecode() {
        // todo Write test cases
    }

    @Test
    fun testApplyExifOrientation() {
        // todo Write test cases
    }

    @Test
    fun testApplyResize() {
        val (_, sketch) = contextAndSketch()
        val newResult: () -> BitmapDecodeResult = {
            BitmapDecodeResult(
                bitmap = Bitmap.createBitmap(80, 50, ARGB_8888),
                imageInfo = ImageInfo(80, 50, "image/png"),
                exifOrientation = 0,
                dataFrom = MEMORY,
                transformedList = null
            )
        }

        /*
         * null
         */
        var resize: Resize? = null
        var result: BitmapDecodeResult = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this === result)
        }

        /*
         * LESS_PIXELS
         */
        // small
        resize = Resize(40, 20, LESS_PIXELS)
        result = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("36x22", this.bitmap.sizeString)
        }
        // big
        resize = Resize(50, 150, LESS_PIXELS)
        result = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this === result)
        }

        /*
         * SAME_ASPECT_RATIO
         */
        // small
        resize = Resize(40, 20, SAME_ASPECT_RATIO)
        result = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("40x20", this.bitmap.sizeString)
        }
        // big
        resize = Resize(50, 150, SAME_ASPECT_RATIO)
        result = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("17x50", this.bitmap.sizeString)
        }

        /*
         * EXACTLY
         */
        // small
        resize = Resize(40, 20, EXACTLY)
        result = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("40x20", this.bitmap.sizeString)
        }
        // big
        resize = Resize(50, 150, EXACTLY)
        result = newResult()
        result.applyResize(sketch, resize).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("50x150", this.bitmap.sizeString)
        }
    }

    @Test
    fun testComputeSizeMultiplier() {
        Assert.assertEquals(0.2, computeSizeMultiplier(1000, 600, 200, 400, true), 0.1)
        Assert.assertEquals(0.6, computeSizeMultiplier(1000, 600, 200, 400, false), 0.1)
        Assert.assertEquals(0.3, computeSizeMultiplier(1000, 600, 400, 200, true), 0.1)
        Assert.assertEquals(0.4, computeSizeMultiplier(1000, 600, 400, 200, false), 0.1)

        Assert.assertEquals(0.6, computeSizeMultiplier(1000, 600, 2000, 400, true), 0.1)
        Assert.assertEquals(2.0, computeSizeMultiplier(1000, 600, 2000, 400, false), 0.1)
        Assert.assertEquals(0.4, computeSizeMultiplier(1000, 600, 400, 2000, true), 0.1)
        Assert.assertEquals(3.3, computeSizeMultiplier(1000, 600, 400, 2000, false), 0.1)

        Assert.assertEquals(2.0, computeSizeMultiplier(1000, 600, 2000, 4000, true), 0.1)
        Assert.assertEquals(6.6, computeSizeMultiplier(1000, 600, 2000, 4000, false), 0.1)
        Assert.assertEquals(3.3, computeSizeMultiplier(1000, 600, 4000, 2000, true), 0.1)
        Assert.assertEquals(4.0, computeSizeMultiplier(1000, 600, 4000, 2000, false), 0.1)
    }

    @Test
    fun testReadImageInfoWithBitmapFactory() {
        val (context, sketch) = contextAndSketch()

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.webp")), "sample.webp")
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                Assert.assertEquals("image/webp", mimeType)
            }

        ResourceDataSource(
            sketch,
            LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
            context.resources,
            R.xml.network_security_config
        )
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(-1, width)
                Assert.assertEquals(-1, height)
                Assert.assertEquals("", mimeType)
            }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrThrow() {
        val (context, sketch) = contextAndSketch()

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.webp")), "sample.webp")
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                Assert.assertEquals("image/webp", mimeType)
            }

        assertThrow(Exception::class) {
            ResourceDataSource(
                sketch,
                LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrThrow()
        }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrNull() {
        val (context, sketch) = contextAndSketch()

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.webp")), "sample.webp")
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                Assert.assertEquals("image/webp", mimeType)
            }

        Assert.assertNull(
            ResourceDataSource(
                sketch,
                LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrNull()
        )
    }

    @Test
    fun testDecodeBitmapWithBitmapFactory() {
        val (context, sketch) = contextAndSketch()

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeBitmapWithBitmapFactory()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeBitmapWithBitmapFactory(BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                Assert.assertEquals(646, width)
                Assert.assertEquals(968, height)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.webp")), "sample.webp")
            .decodeBitmapWithBitmapFactory()!!.apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
            }

        Assert.assertNull(
            ResourceDataSource(
                sketch,
                LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).decodeBitmapWithBitmapFactory()
        )
    }

    @Test
    fun testDecodeRegionBitmap() {
        val (context, sketch) = contextAndSketch()

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeRegionBitmap(Rect(500, 500, 600, 600))!!.apply {
                Assert.assertEquals(100, width)
                Assert.assertEquals(100, height)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.jpeg")), "sample.jpeg")
            .decodeRegionBitmap(
                Rect(500, 500, 600, 600),
                BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                Assert.assertEquals(50, width)
                Assert.assertEquals(50, height)
            }

        AssetDataSource(sketch, LoadRequest(context, newAssetUri("sample.webp")), "sample.webp")
            .decodeRegionBitmap(Rect(500, 500, 700, 700))!!.apply {
                Assert.assertEquals(200, width)
                Assert.assertEquals(200, height)
            }

        assertThrow(IOException::class) {
            ResourceDataSource(
                sketch,
                LoadRequest(context, context.newResourceUri(R.xml.network_security_config)),
                context.resources,
                R.xml.network_security_config
            ).decodeRegionBitmap(Rect(500, 500, 600, 600))
        }
    }

    @Test
    fun testSupportBitmapRegionDecoder() {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            Assert.assertTrue(ImageFormat.HEIC.supportBitmapRegionDecoder())
        } else {
            Assert.assertFalse(ImageFormat.HEIC.supportBitmapRegionDecoder())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            Assert.assertTrue(ImageFormat.HEIF.supportBitmapRegionDecoder())
        } else {
            Assert.assertFalse(ImageFormat.HEIF.supportBitmapRegionDecoder())
        }
        Assert.assertFalse(ImageFormat.BMP.supportBitmapRegionDecoder())
        Assert.assertFalse(ImageFormat.GIF.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.JPEG.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.PNG.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.WEBP.supportBitmapRegionDecoder())
    }

    @Test
    fun testIsInBitmapError() {
        Assert.assertTrue(
            isInBitmapError(IllegalArgumentException("Problem decoding into existing bitmap"))
        )
        Assert.assertTrue(
            isInBitmapError(IllegalArgumentException("bitmap"))
        )

        Assert.assertFalse(
            isInBitmapError(IllegalArgumentException("Problem decoding"))
        )
        Assert.assertFalse(
            isInBitmapError(IllegalStateException("Problem decoding into existing bitmap"))
        )
    }

    @Test
    fun testIsSrcRectError() {
        Assert.assertTrue(
            isSrcRectError(IllegalArgumentException("rectangle is outside the image srcRect"))
        )
        Assert.assertTrue(
            isSrcRectError(IllegalArgumentException("srcRect"))
        )

        Assert.assertFalse(
            isSrcRectError(IllegalStateException("rectangle is outside the image srcRect"))
        )
        Assert.assertFalse(
            isSrcRectError(IllegalArgumentException(""))
        )
    }
}