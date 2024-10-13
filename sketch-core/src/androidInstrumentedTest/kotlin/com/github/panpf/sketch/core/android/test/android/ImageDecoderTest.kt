package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.ImageDecoder
import android.os.Build.VERSION
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.decodeImageUseImageDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageDecoderTest {

    // TODO test format

    @Test
    fun testMutable() {
        if (VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, ResourceImages.jpeg.resourceName)
            .also { bitmap ->
                assertFalse(bitmap.isMutable)
            }

        decodeImageUseImageDecoder(
            context,
            ResourceImages.jpeg.resourceName,
            mutable = true
        ).also { bitmap ->
            assertTrue(bitmap.isMutable)
        }
    }

    @Test
    fun testConfig() {
        if (VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, ResourceImages.jpeg.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.png.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.bmp.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.webp.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.heic.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.animGif.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.animWebp.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, ResourceImages.animHeif.resourceName).also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }
    }

    @Test
    fun testHasAlpha() {
        if (VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, ResourceImages.jpeg.resourceName).also { bitmap ->
            assertFalse(bitmap.hasAlpha())
        }

        decodeImageUseImageDecoder(context, ResourceImages.png.resourceName).also { bitmap ->
            assertTrue(bitmap.hasAlpha())
        }
    }

    @Test
    fun testInSampleSize() {
        listOf(
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.jpeg.resourceName,
                size = Size(1291, 1936),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.png.resourceName,
                size = Size(750, 719),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.bmp.resourceName,
                size = Size(700, 1012),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.webp.resourceName,
                size = Size(1080, 1344),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.heic.resourceName,
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.animGif.resourceName,
                size = Size(480, 480),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.animWebp.resourceName,
                size = Size(480, 270),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.animHeif.resourceName,
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
                    assertSizeEquals(
                        expected = sampledBitmapSize,
                        actual = bitmap.size,
                        delta = Size(1, 1),
                        message = message
                    )
                } else {
                    assertEquals(imageSize, bitmap.size, message)
                }
            }
        } else {
            assertFailsWith(NoClassDefFoundError::class) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.assets, image.assetName)
                )
            }
        }
    }
}