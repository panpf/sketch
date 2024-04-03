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
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.AssetImages
import com.github.panpf.sketch.core.test.android.internal.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.decodeImageUseImageDecoder
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

        decodeImageUseImageDecoder(context, AssetImages.jpeg.fileName)
            .also { bitmap ->
                Assert.assertFalse(bitmap.isMutable)
            }

        decodeImageUseImageDecoder(
            context,
            AssetImages.jpeg.fileName,
            mutable = true
        ).also { bitmap ->
            Assert.assertTrue(bitmap.isMutable)
        }
    }

    @Test
    fun testConfig() {
        if (Build.VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, AssetImages.jpeg.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.png.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.bmp.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.webp.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.heic.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.animGif.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.animWebp.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }

        decodeImageUseImageDecoder(context, AssetImages.animHeif.fileName).also { bitmap ->
            Assert.assertEquals(Bitmap.Config.HARDWARE, bitmap.config)
        }
    }

    @Test
    fun testHasAlpha() {
        if (Build.VERSION.SDK_INT < 28) return
        val context = getTestContext()

        decodeImageUseImageDecoder(context, AssetImages.jpeg.fileName).also { bitmap ->
            Assert.assertFalse(bitmap.hasAlpha())
        }

        decodeImageUseImageDecoder(context, AssetImages.png.fileName).also { bitmap ->
            Assert.assertTrue(bitmap.hasAlpha())
        }
    }

    @Test
    fun testInSampleSize() {
        listOf(
            ImageDecodeCompatibility(
                assetName = AssetImages.jpeg.fileName,
                size = Size(1291, 1936),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.png.fileName,
                size = Size(750, 719),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.bmp.fileName,
                size = Size(700, 1012),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.webp.fileName,
                size = Size(1080, 1344),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.heic.fileName,
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.animGif.fileName,
                size = Size(480, 480),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.animWebp.fileName,
                size = Size(480, 270),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.animHeif.fileName,
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