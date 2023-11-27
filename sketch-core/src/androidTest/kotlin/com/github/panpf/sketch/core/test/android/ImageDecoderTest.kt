@file:Suppress("DEPRECATION")

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
package com.github.panpf.sketch.core.test.android

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.decodeImageUseImageDecoder
import com.github.panpf.sketch.core.test.getTestContext
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
                assetName = "sample.jpeg",
                size = Size(1291, 1936),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample.png",
                size = Size(750, 719),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample.bmp",
                size = Size(700, 1012),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample.webp",
                size = Size(1080, 1344),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample.heic",
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample_anim.gif",
                size = Size(480, 480),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample_anim.webp",
                size = Size(480, 270),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = "sample_anim.heif",
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
        image: ImageDecodeCompatibility,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        val context = getTestContext()
        val message = "enabledInBitmap=$enabledInBitmap, sampleSize=$sampleSize. $image"
        val extension = image.assetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"
        val imageSize = image.size

        if (Build.VERSION.SDK_INT >= image.minAPI) {
            try {
                decodeImageUseImageDecoder(context, image.assetName, sampleSize)
            } catch (e: IllegalArgumentException) {
                throw Exception(message, e)
            }.also { bitmap ->
                if (sampleSize > 1 && Build.VERSION.SDK_INT >= image.inSampleSizeMinAPI) {
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