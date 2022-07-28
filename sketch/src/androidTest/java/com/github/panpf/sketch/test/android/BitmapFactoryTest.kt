@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.test.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.fetch.internal.HeaderBytes
import com.github.panpf.sketch.fetch.internal.isAnimatedWebP
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
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
    fun testInBitmapAndInSampleSize() {
        listOf(
            ImageDecodeCompatibility(
                imageAssetName = "sample.jpeg",
                imageSize = Size(1291, 1936),
                minAPI = 16,
                sampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inBitmapAndInSampleSizeMinAPI = 19
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.png",
                imageSize = Size(750, 719),
                minAPI = 16,
                sampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inBitmapAndInSampleSizeMinAPI = 19
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.bmp",
                imageSize = Size(700, 1012),
                minAPI = 16,
                sampleSizeMinAPI = 16,
                inBitmapMinAPI = 19,
                inBitmapAndInSampleSizeMinAPI = 19,
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.webp",
                imageSize = Size(1080, 1344),
                minAPI = 16,
                sampleSizeMinAPI = 16,
                inBitmapMinAPI = 19,
                inBitmapAndInSampleSizeMinAPI = 19,
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.heic",
                imageSize = Size(750, 932),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1,
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample_anim.gif",
                imageSize = Size(480, 480),
                minAPI = 16,
                sampleSizeMinAPI = 21,
                inBitmapMinAPI = 19,
                inBitmapAndInSampleSizeMinAPI = 19,
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample_anim.webp",
                imageSize = Size(480, 270),
                minAPI = 26,
                sampleSizeMinAPI = 26,
                inBitmapMinAPI = 26,
                inBitmapAndInSampleSizeMinAPI = 26,
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample_anim.heif",
                imageSize = Size(256, 144),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = 28,
                inBitmapAndInSampleSizeMinAPI = 28,
            ),
        ).forEach {
            testDecodeImage(image = it, enabledInBitmap = false, sampleSize = 1)
            testDecodeImage(image = it, enabledInBitmap = false, sampleSize = 2)
            testDecodeImage(image = it, enabledInBitmap = true, sampleSize = 1)
            testDecodeImage(image = it, enabledInBitmap = true, sampleSize = 2)
        }
    }

    private fun testDecodeImage(
        image: ImageDecodeCompatibility,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        if (image.minAPI == -1) return
        val context = getTestContext()
        val decodeWithInBitmap: (options: BitmapFactory.Options) -> Bitmap? = { options ->
            context.assets.open(image.imageAssetName).use {
                BitmapFactory.decodeStream(it, null, options)
            }
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        val message = "${image.imageAssetName}(enabledInBitmap=$enabledInBitmap,sampleSize=$sampleSize)"
        val extension = image.imageAssetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"
        val imageSize = image.imageSize

        if (Build.VERSION.SDK_INT >= image.minAPI) {
            val sampledBitmapSize = calculateSampledBitmapSize(
                imageSize = imageSize,
                sampleSize = options.inSampleSize,
                mimeType = mimeType
            )
            if (enabledInBitmap) {
                options.inBitmap = Bitmap.createBitmap(
                    sampledBitmapSize.width,
                    sampledBitmapSize.height,
                    Bitmap.Config.ARGB_8888
                )
                if (Build.VERSION.SDK_INT >= image.inBitmapMinAPI && (sampleSize == 1 || Build.VERSION.SDK_INT >= image.inBitmapAndInSampleSizeMinAPI)) {
                    decodeWithInBitmap(options)!!.also { bitmap ->
                        Assert.assertSame(message, options.inBitmap, bitmap)
                        if (Build.VERSION.SDK_INT >= image.sampleSizeMinAPI) {
                            Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                        } else {
                            Assert.assertEquals(message, imageSize, bitmap.size)
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
                    if (Build.VERSION.SDK_INT >= image.sampleSizeMinAPI) {
                        Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                    } else {
                        Assert.assertEquals(message, imageSize, bitmap.size)
                    }
                }
            }
        } else {
            val headerBytes = HeaderBytes(
                ByteArray(1024).apply {
                    context.assets.open(image.imageAssetName).use { it.read(this) }
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