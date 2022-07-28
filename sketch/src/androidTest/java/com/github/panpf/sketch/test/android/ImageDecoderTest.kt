@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.test.android

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.decodeImageUseImageDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageDecoderTest {

    @Test
    fun testMutable() {
        if (Build.VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, "sample.jpeg")
            .also { bitmap ->
                Assert.assertFalse(bitmap.isMutable)
            }

        decodeImageUseImageDecoder(context, "sample.jpeg", mutable = true).also { bitmap ->
            Assert.assertTrue(bitmap.isMutable)
        }
    }

    @Test
    fun testConfig() {
        if (Build.VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, "sample.jpeg").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample.png").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample.bmp").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample.webp").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample.heic").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample_anim.gif").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample_anim.webp").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, "sample_anim.heif").also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }
    }

    @Test
    fun testHasAlpha() {
        if (Build.VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, "sample.jpeg").also { bitmap ->
            Assert.assertFalse(bitmap.hasAlpha())
        }

        decodeImageUseImageDecoder(context, "sample.png").also { bitmap ->
            Assert.assertTrue(bitmap.hasAlpha())
        }
    }

    @Test
    fun testInSampleSize() {
        listOf(
            ImageDecodeCompatibility(
                imageAssetName = "sample.jpeg",
                imageSize = Size(1291, 1936),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.png",
                imageSize = Size(750, 719),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.bmp",
                imageSize = Size(700, 1012),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.webp",
                imageSize = Size(1080, 1344),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample.heic",
                imageSize = Size(750, 932),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample_anim.gif",
                imageSize = Size(480, 480),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample_anim.webp",
                imageSize = Size(480, 270),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageAssetName = "sample_anim.heif",
                imageSize = Size(256, 144),
                minAPI = 28,
                sampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inBitmapAndInSampleSizeMinAPI = -1
            ),
        ).forEach {
            testDecodeImage(image = it, enabledInBitmap = false, sampleSize = 1)
            testDecodeImage(image = it, enabledInBitmap = false, sampleSize = 2)
            testDecodeImage(image = it, enabledInBitmap = true, sampleSize = 1)
            testDecodeImage(image = it, enabledInBitmap = true, sampleSize = 2)
        }
    }

    @Test
    fun test() {

    }

    private fun testDecodeImage(
        image: ImageDecodeCompatibility,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        val context = getTestContext()
        val message =
            "${image.imageAssetName}(enabledInBitmap=$enabledInBitmap,sampleSize=$sampleSize)"
        val extension = image.imageAssetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"
        val imageSize = image.imageSize

        if (Build.VERSION.SDK_INT >= image.minAPI) {
            decodeImageUseImageDecoder(
                context, image.imageAssetName, sampleSize = sampleSize
            ).also { bitmap ->
                if (Build.VERSION.SDK_INT >= image.sampleSizeMinAPI) {
                    val sampledBitmapSize = calculateSampledBitmapSize(
                        imageSize = imageSize,
                        sampleSize = sampleSize,
                        mimeType = mimeType
                    )
                    Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                } else {
                    Assert.assertEquals(message, imageSize, bitmap.size)
                }
            }
        } else {
            assertThrow(NoClassDefFoundError::class) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.assets, image.imageAssetName)
                )
            }
        }
    }
}