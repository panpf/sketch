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
package com.github.panpf.sketch.gif.test.androiod

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.isAnimatedWebP
import com.github.panpf.sketch.gif.test.getTestContext
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toShortInfoString
import com.github.panpf.sketch.util.Bytes
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapFactoryTest {

    @Test
    fun testInBitmapAndInSampleSize() {
        listOf(
            ImageDecodeCompatibility(
                assetName = "sample_anim.gif",
                size = Size(480, 480),
                minAPI = 16,
                inSampleSizeMinAPI = 21,
                inBitmapMinAPI = 19,
                inSampleSizeOnInBitmapMinAPI = 21,
            ),
            ImageDecodeCompatibility(
                assetName = "sample_anim.webp",
                size = Size(480, 270),
                minAPI = 26,
                inSampleSizeMinAPI = 26,
                inBitmapMinAPI = 26,
                inSampleSizeOnInBitmapMinAPI = 26,
            ),
            ImageDecodeCompatibility(
                assetName = "sample_anim.heif",
                size = Size(256, 144),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = 28,
                inSampleSizeOnInBitmapMinAPI = 28,
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
            val bytes = Bytes(
                ByteArray(1024).apply { context.assets.open(image.assetName).use { it.read(this) } }
            )
            if (bytes.isAnimatedWebP() && Build.VERSION.SDK_INT == 17) {
                Assert.assertNotNull(message, bitmap)
            } else {
                Assert.assertNull(message, bitmap)
            }
        }
    }
}