package com.github.panpf.sketch.test.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForBitmapRegionDecoder
import com.github.panpf.sketch.fetch.internal.HeaderBytes
import com.github.panpf.sketch.fetch.internal.isAnimatedWebP
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newBitmapRegionDecoderInstanceCompat
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class BitmapRegionDecoderTest {

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options()
        Assert.assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        Assert.assertFalse(bitmap.isMutable)

        options.inMutable = true
        Assert.assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        Assert.assertFalse(bitmap1.isMutable)
    }

    @Test
    fun testInSampleSize() {
        val context = getTestContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }

        Rect(0, 0, imageSize.width, imageSize.height).let { rect ->
            context.assets.open(imageName)
                .run { newBitmapRegionDecoderInstanceCompat() }!!
                .use { decodeRegion(rect, options) }!!
                .also { bitmap ->
                    val sampledBitmapSize = calculateSampledBitmapSizeForBitmapRegionDecoder(
                        regionSize = Size(rect.width(), rect.height()),
                        sampleSize = options.inSampleSize,
                        imageSize = imageSize
                    )
                    Assert.assertEquals(
                        sampledBitmapSize,
                        bitmap.size
                    )
                }
        }

        Rect(0, 0, imageSize.width / 2, imageSize.height).let { rect ->
            context.assets.open(imageName)
                .run { newBitmapRegionDecoderInstanceCompat() }!!
                .use { decodeRegion(rect, options) }!!
                .also { bitmap ->
                    val sampledBitmapSize = calculateSampledBitmapSizeForBitmapRegionDecoder(
                        regionSize = Size(rect.width(), rect.height()),
                        sampleSize = options.inSampleSize,
                        imageSize = imageSize
                    )
                    Assert.assertEquals(
                        sampledBitmapSize,
                        bitmap.size
                    )
                }
        }

        Rect(0, 0, imageSize.width, imageSize.height / 2).let { rect ->
            context.assets.open(imageName)
                .run { newBitmapRegionDecoderInstanceCompat() }!!
                .use { decodeRegion(rect, options) }!!
                .also { bitmap ->
                    val sampledBitmapSize = calculateSampledBitmapSizeForBitmapRegionDecoder(
                        regionSize = Size(rect.width(), rect.height()),
                        sampleSize = options.inSampleSize,
                        imageSize = imageSize
                    )
                    Assert.assertEquals(
                        sampledBitmapSize,
                        bitmap.size
                    )
                }
        }

        Rect(0, 0, imageSize.width / 2, imageSize.height / 2).let { rect ->
            context.assets.open(imageName)
                .run { newBitmapRegionDecoderInstanceCompat() }!!
                .use { decodeRegion(rect, options) }!!
                .also { bitmap ->
                    val sampledBitmapSize = calculateSampledBitmapSizeForBitmapRegionDecoder(
                        regionSize = Size(rect.width(), rect.height()),
                        sampleSize = options.inSampleSize,
                        imageSize = imageSize
                    )
                    Assert.assertEquals(
                        sampledBitmapSize,
                        bitmap.size
                    )
                }
        }
    }

    @Test
    fun testInPreferredConfig() {
        val context = getTestContext()
        val imageName = "sample.jpeg"
        val imageSize = Size(1291, 1936)

        val options = BitmapFactory.Options()
        Assert.assertEquals(Bitmap.Config.ARGB_8888, options.inPreferredConfig)
        val bitmap = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        Assert.assertEquals(Bitmap.Config.ARGB_8888, bitmap.config)

        @Suppress("DEPRECATION")
        options.inPreferredConfig = Bitmap.Config.ARGB_4444
        @Suppress("DEPRECATION")
        Assert.assertEquals(Bitmap.Config.ARGB_4444, options.inPreferredConfig)
        val bitmap1 = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, bitmap1.config)
        } else {
            @Suppress("DEPRECATION")
            Assert.assertEquals(Bitmap.Config.ARGB_4444, bitmap1.config)
        }
    }

    @Test
    fun testInBitmapJPEG() {
        decodeRegion(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_JPEG_URI, 1291, 1936,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapPNG() {
        decodeRegion(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_PNG_URI, 750, 719,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapBMP() {
        decodeRegion(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = -1,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_BMP_URI, 700, 1012,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapWEBP() {
        decodeRegion(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_WEBP_URI, 1080, 1344,
            minAPI = 16,
            sampleSizeMinAPI = 16,
            inBitmapMinAPI = 16,
            inBitmapAndInSampleSizeMinAPI = 16,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapHEIC() {
        decodeRegion(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_HEIC_URI, 750, 932,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapGIF() {
        decodeRegion(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_GIF_URI, 480, 480,
            minAPI = -1,
            sampleSizeMinAPI = -1,
            inBitmapMinAPI = -1,
            inBitmapAndInSampleSizeMinAPI = -1,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    @Test
    fun testInBitmapAnimWEBP() {
        decodeRegion(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_WEBP_URI, 480, 270,
            minAPI = 26,
            sampleSizeMinAPI = 26,
            inBitmapMinAPI = 26,
            inBitmapAndInSampleSizeMinAPI = 26,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
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
        decodeRegion(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = false,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = false,
            sampleSize = 2
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = true,
            sampleSize = 1
        )
        decodeRegion(
            TestAssets.SAMPLE_ANIM_HEIf_URI, 256, 144,
            minAPI = 28,
            sampleSizeMinAPI = 28,
            inBitmapMinAPI = 28,
            inBitmapAndInSampleSizeMinAPI = 28,
            enabledInBitmap = true,
            sampleSize = 2
        )
    }

    private fun decodeRegion(
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
        val context = getTestContext()
        val itemWidth = imageWidth / 3
        val itemHeight = imageHeight / 3
        val regionRect =
            Rect(itemWidth, itemHeight, itemWidth + itemWidth, itemHeight + itemHeight).apply {
                if (width() % 2 == 0) right++
                if (height() % 2 == 0) bottom++
            }
        val assetName = assetUri.toUri().authority!!
        val decodeWithInBitmap: (options: BitmapFactory.Options) -> Bitmap? = { options ->
            context.assets.open(assetName).use {
                it.newBitmapRegionDecoderInstanceCompat()!!.decodeRegion(regionRect, options)
            }
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inMutable = true
        }
        val message =
            "$assetUri(regionRect=$regionRect,enabledInBitmap=$enabledInBitmap,sampleSize=$sampleSize)"

        if (minAPI != -1 && Build.VERSION.SDK_INT >= minAPI) {
            val sampledBitmapSize =
                calculateSampledBitmapSizeForBitmapRegionDecoder(
                    regionSize = Size(regionRect.width(), regionRect.height()),
                    sampleSize = options.inSampleSize,
                    imageSize = Size(imageWidth, imageHeight)
                )
            if (enabledInBitmap) {
                options.inBitmap = Bitmap.createBitmap(
                    sampledBitmapSize.width,
                    sampledBitmapSize.height,
                    Bitmap.Config.ARGB_8888
                )
                if (Build.VERSION.SDK_INT >= inBitmapMinAPI && (sampleSize == 1 || Build.VERSION.SDK_INT >= inBitmapAndInSampleSizeMinAPI)) {
                    decodeWithInBitmap(options)!!.also { bitmap ->
                        Assert.assertSame(message, options.inBitmap, bitmap)
                        if (Build.VERSION.SDK_INT >= sampleSizeMinAPI) {
                            Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                        } else {
                            Assert.assertEquals(
                                message,
                                Size(regionRect.width(), regionRect.height()),
                                bitmap.size
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
                        Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                    } else {
                        Assert.assertEquals(
                            message,
                            Size(regionRect.width(), regionRect.height()),
                            bitmap.size
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
            if (headerBytes.isAnimatedWebP()) {
                when (Build.VERSION.SDK_INT) {
                    16 -> assertThrow(IOException::class) {
                        decodeWithInBitmap(options)
                    }
                    17 -> Assert.assertNotNull(decodeWithInBitmap(options))
                    else -> Assert.assertNull(decodeWithInBitmap(options))
                }
            } else {
                assertThrow(IOException::class) {
                    decodeWithInBitmap(options)
                }
            }
        }
    }

    private fun <R> BitmapRegionDecoder.use(block: BitmapRegionDecoder.() -> R): R {
        try {
            return block(this)
        } finally {
            recycle()
        }
    }
}