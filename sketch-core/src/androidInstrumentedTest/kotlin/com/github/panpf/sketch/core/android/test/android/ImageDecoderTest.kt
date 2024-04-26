package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.ImageDecoder
import android.os.Build.VERSION
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.utils.decodeImageUseImageDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.tools4j.test.ktx.assertThrow

@RunWith(AndroidJUnit4::class)
class ImageDecoderTest {

    @Test
    fun testMutable() {
        if (VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, MyImages.jpeg.fileName)
            .also { bitmap ->
                Assert.assertFalse(bitmap.isMutable)
            }

        decodeImageUseImageDecoder(
            context,
            MyImages.jpeg.fileName,
            mutable = true
        ).also { bitmap ->
            Assert.assertTrue(bitmap.isMutable)
        }
    }

    @Test
    fun testConfig() {
        if (VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, MyImages.jpeg.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.png.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.bmp.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.webp.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.heic.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.animGif.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.animWebp.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, MyImages.animHeif.fileName).also { bitmap ->
            Assert.assertEquals(HARDWARE, bitmap.config)
        }
    }

    @Test
    fun testHasAlpha() {
        if (VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, MyImages.jpeg.fileName).also { bitmap ->
            Assert.assertFalse(bitmap.hasAlpha())
        }

        decodeImageUseImageDecoder(context, MyImages.png.fileName).also { bitmap ->
            Assert.assertTrue(bitmap.hasAlpha())
        }
    }

    @Test
    fun testInSampleSize() {
        listOf(
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.jpeg.fileName,
                size = Size(1291, 1936),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.png.fileName,
                size = Size(750, 719),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.bmp.fileName,
                size = Size(700, 1012),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.webp.fileName,
                size = Size(1080, 1344),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.heic.fileName,
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.animGif.fileName,
                size = Size(480, 480),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.animWebp.fileName,
                size = Size(480, 270),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = MyImages.animHeif.fileName,
                size = Size(256, 144),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
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
        image: com.github.panpf.sketch.test.utils.ImageDecodeCompatibility,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        val context = getTestContext()
        val message = "enabledInBitmap=$enabledInBitmap, sampleSize=$sampleSize. $image"
        val extension = image.assetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"
        val imageSize = image.size

        if (VERSION.SDK_INT >= image.minAPI) {
            try {
                decodeImageUseImageDecoder(context, image.assetName, sampleSize)
            } catch (e: IllegalArgumentException) {
                throw Exception(message, e)
            }.also { bitmap ->
                if (sampleSize > 1 && VERSION.SDK_INT >= image.inSampleSizeMinAPI) {
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
                    ImageDecoder.createSource(context.assets, image.assetName)
                )
            }
        }
    }
}