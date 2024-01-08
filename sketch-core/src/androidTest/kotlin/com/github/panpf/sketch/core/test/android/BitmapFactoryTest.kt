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
import android.graphics.BitmapFactory
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapFactoryTest {

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = AssetImages.jpeg.fileName

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
        val imageName = AssetImages.jpeg.fileName

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

        context.assets.open(AssetImages.jpeg.fileName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, config)
            Assert.assertFalse(hasAlpha())
        }

        context.assets.open(AssetImages.png.fileName).use {
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
                assetName = AssetImages.jpeg.fileName,
                size = Size(1291, 1936),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inSampleSizeOnInBitmapMinAPI = 19
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.png.fileName,
                size = Size(750, 719),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inSampleSizeOnInBitmapMinAPI = 19
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.bmp.fileName,
                size = Size(700, 1012),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 19,
                inSampleSizeOnInBitmapMinAPI = 19,
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.webp.fileName,
                size = Size(1080, 1344),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 19,
                inSampleSizeOnInBitmapMinAPI = 19,
            ),
            ImageDecodeCompatibility(
                assetName = AssetImages.heic.fileName,
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1,
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
        val context = getTestContext()
        val decodeWithInBitmap: (options: BitmapFactory.Options) -> Bitmap? = { options ->
            context.assets.open(image.assetName).use {
                BitmapFactory.decodeStream(it, null, options)
            }
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        val message =
            "enabledInBitmap=$enabledInBitmap, sampleSize=$sampleSize, sdk=${Build.VERSION.SDK_INT}. $image"
        val extension = image.assetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"

        if (image.minAPI != -1 && Build.VERSION.SDK_INT >= image.minAPI) {
            val sampledBitmapSize = calculateSampledBitmapSize(
                imageSize = image.size,
                sampleSize = options.inSampleSize,
                mimeType = mimeType
            )
            if (enabledInBitmap) {
                if (Build.VERSION.SDK_INT >= image.inBitmapMinAPI) {
                    if (sampleSize > 1) {
                        options.inBitmap = Bitmap.createBitmap(
                            sampledBitmapSize.width,
                            sampledBitmapSize.height,
                            Bitmap.Config.ARGB_8888
                        )
                        if (Build.VERSION.SDK_INT >= image.inSampleSizeOnInBitmapMinAPI) {
                            try {
                                decodeWithInBitmap(options)!!
                            } catch (e: IllegalArgumentException) {
                                throw Exception(message, e)
                            }.also { bitmap ->
                                Assert.assertSame(message, options.inBitmap, bitmap)
                                Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                            }
                        } else {
                            /* sampleSize not support */
                            if (ImageFormat.GIF.matched(mimeType) && Build.VERSION.SDK_INT == 19) {
                                try {
                                    decodeWithInBitmap(options)!!
                                } catch (e: IllegalArgumentException) {
                                    throw Exception(message, e)
                                }.also { bitmap ->
                                    Assert.assertSame(message, options.inBitmap, bitmap)
                                    Assert.assertEquals(message, image.size, bitmap.size)
                                }
                            } else {
                                try {
                                    val bitmap = decodeWithInBitmap(options)!!
                                    Assert.fail("inBitmapAndInSampleSizeMinAPI error. bitmap=${bitmap.toShortInfoString()}. $message")
                                } catch (e: IllegalArgumentException) {
                                    if (e.message != "Problem decoding into existing bitmap") {
                                        throw Exception("exception type error. $message", e)
                                    }
                                }
                            }
                        }
                    } else {
                        /* sampleSize 1 */
                        options.inBitmap = Bitmap.createBitmap(
                            image.size.width,
                            image.size.height,
                            Bitmap.Config.ARGB_8888
                        )
                        try {
                            decodeWithInBitmap(options)!!
                        } catch (e: IllegalArgumentException) {
                            throw Exception(message, e)
                        }.also { bitmap ->
                            Assert.assertSame(message, options.inBitmap, bitmap)
                            Assert.assertEquals(message, image.size, bitmap.size)
                        }
                    }
                } else {
                    /* inBitmapMinAPI not support */
                    options.inBitmap = Bitmap.createBitmap(
                        image.size.width,
                        image.size.height,
                        Bitmap.Config.ARGB_8888
                    )
                    try {
                        val bitmap = decodeWithInBitmap(options)!!
                        Assert.fail("inBitmapMinAPI error. bitmap=${bitmap.toShortInfoString()}. $message")
                    } catch (e: IllegalArgumentException) {
                        if (e.message != "Problem decoding into existing bitmap") {
                            throw Exception("exception type error. $message", e)
                        }
                    }
                }
            } else {
                /* enabledInBitmap false */
                val bitmap = try {
                    decodeWithInBitmap(options)!!
                } catch (e: IllegalArgumentException) {
                    throw Exception(message, e)
                }
                if (sampleSize > 1 && Build.VERSION.SDK_INT >= image.inSampleSizeMinAPI) {
                    Assert.assertEquals(message, sampledBitmapSize, bitmap.size)
                } else {
                    Assert.assertEquals(message, image.size, bitmap.size)
                }
            }
        } else {
            /* minAPI not support */
            val bitmap = try {
                decodeWithInBitmap(options)
            } catch (e: IllegalArgumentException) {
                throw Exception(message, e)
            }
            Assert.assertNull(message, bitmap)
        }
    }
}