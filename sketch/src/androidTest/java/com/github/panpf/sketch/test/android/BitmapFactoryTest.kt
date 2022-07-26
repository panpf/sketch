@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.test.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.sampling
import com.github.panpf.sketch.decode.internal.samplingSize
import com.github.panpf.sketch.fetch.internal.HeaderBytes
import com.github.panpf.sketch.fetch.internal.isAnimatedWebP
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapFactoryTest {

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = "sample.jpeg"

        val options = BitmapFactory.Options()
        Assert.assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertFalse(bitmap.isMutable)

        options.inMutable = true
        Assert.assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertTrue(bitmap1.isMutable)
    }

    @Test
    fun testInSampleSize() {
        val context = getTestContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertEquals(imageSize.sampling(2), bitmap.size)
    }

    @Test
    fun testInPreferredConfig() {
        val context = getTestContext()
        val imageName = "sample.jpeg"

        val options = BitmapFactory.Options()
        Assert.assertEquals(Bitmap.Config.ARGB_8888, options.inPreferredConfig)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        Assert.assertEquals(Bitmap.Config.ARGB_8888, bitmap.config)

        options.inPreferredConfig = Bitmap.Config.ARGB_4444
        Assert.assertEquals(Bitmap.Config.ARGB_4444, options.inPreferredConfig)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, bitmap1.config)
        } else {
            Assert.assertEquals(Bitmap.Config.ARGB_4444, bitmap1.config)
        }
    }

    @Test
    fun testHasAlpha() {
        val context = getTestContext()

        context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, config)
            Assert.assertFalse(hasAlpha())
        }

        context.assets.open("sample.png").use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, config)
            Assert.assertTrue(hasAlpha())
        }
    }

    @Test
    fun testInBitmapJPEG() {
        testDecodeImage(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapPNG() {
        testDecodeImage(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapBMP() {
        testDecodeImage(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapWEBP() {
        testDecodeImage(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapHEIC() {
        testDecodeImage(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapGIF() {
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = 16,
            sampleSizeMinAPI = 21,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = 16,
            sampleSizeMinAPI = 21,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = 16,
            sampleSizeMinAPI = 21,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = 16,
            sampleSizeMinAPI = 21,
            inBitmapMinAPI = 19,
            inBitmapAndInSampleSizeMinAPI = 19,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapAnimWEBP() {
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapAnimHEIF() {
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = false,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = false,
            sampleSize = 2
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = true,
            sampleSize = 1
        )
        testDecodeImage(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    private fun testDecodeImage(
        assetUri: String,
        imageWidth: Int,
        imageHeight: Int,
        minAPI: Int,
        sampleSizeMinAPI: Int,
        inBitmapMinAPI: Int,
        inBitmapAndInSampleSizeMinAPI: Int,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        if (minAPI == -1) return
        val context = getTestContext()
        val assetName = assetUri.toUri().authority!!
        val decodeWithInBitmap: (options: BitmapFactory.Options) -> Bitmap? = { options ->
            context.assets.open(assetName).use {
                BitmapFactory.decodeStream(it, null, options)
            }
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inMutable = true
        }
        val message = "$assetUri(enabledInBitmap=$enabledInBitmap,sampleSize=$sampleSize)"
        val extension = assetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"

        if (Build.VERSION.SDK_INT >= minAPI) {
            if (enabledInBitmap) {
                options.inBitmap = Bitmap.createBitmap(
                    samplingSize(imageWidth, options.inSampleSize, mimeType),
                    samplingSize(imageHeight, options.inSampleSize, mimeType),
                    Bitmap.Config.ARGB_8888
                )
                if (Build.VERSION.SDK_INT >= inBitmapMinAPI && (sampleSize == 1 || Build.VERSION.SDK_INT >= inBitmapAndInSampleSizeMinAPI)) {
                    decodeWithInBitmap(options)!!.also { bitmap ->
                        Assert.assertSame(message, options.inBitmap, bitmap)
                        if (Build.VERSION.SDK_INT >= sampleSizeMinAPI) {
                            Assert.assertEquals(
                                message,
                                "%dx%d".format(
                                    samplingSize(imageWidth, options.inSampleSize, mimeType),
                                    samplingSize(imageHeight, options.inSampleSize, mimeType)
                                ),
                                "${bitmap.width}x${bitmap.height}"
                            )
                        } else {
                            Assert.assertEquals(
                                message,
                                "${imageWidth}x${imageHeight}",
                                "${bitmap.width}x${bitmap.height}"
                            )
                        }
                    }
                } else {
                    try {
                        decodeWithInBitmap(options)!!
                        Assert.fail("$message. inBitmapMinAPI or inBitmapAndInSampleSizeMinAPI error")
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        if (e.message != "Problem decoding into existing bitmap") {
                            Assert.fail("$message. exception type error: $e")
                        }
                    }
                }
            } else {
                decodeWithInBitmap(options)!!.also { bitmap ->
                    if (Build.VERSION.SDK_INT >= sampleSizeMinAPI) {
                        Assert.assertEquals(
                            message,
                            "%dx%d".format(
                                samplingSize(imageWidth, options.inSampleSize, mimeType),
                                samplingSize(imageHeight, options.inSampleSize, mimeType)
                            ),
                            "${bitmap.width}x${bitmap.height}"
                        )
                    } else {
                        Assert.assertEquals(
                            message,
                            "${imageWidth}x${imageHeight}",
                            "${bitmap.width}x${bitmap.height}"
                        )
                    }
                }
            }
        } else {
            val headerBytes = HeaderBytes(
                ByteArray(1024).apply {
                    context.assets.open(assetName).use { it.read(this) }
                }
            )
            if (headerBytes.isAnimatedWebP() && Build.VERSION.SDK_INT == 17) {
                Assert.assertNotNull(message, decodeWithInBitmap(options))
            } else {
                Assert.assertNull(message, decodeWithInBitmap(options))
            }
        }
    }
}