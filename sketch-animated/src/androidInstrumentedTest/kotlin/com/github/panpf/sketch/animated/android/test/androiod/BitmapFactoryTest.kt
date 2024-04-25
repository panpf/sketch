package com.github.panpf.sketch.animated.android.test.androiod

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Build.VERSION
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ImageFormat.GIF
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.isAnimatedWebP
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.getTestContext
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
                assetName = MyImages.animGif.fileName,
                size = Size(480, 480),
                minAPI = 16,
                inSampleSizeMinAPI = 21,
                inBitmapMinAPI = 19,
                inSampleSizeOnInBitmapMinAPI = 21,
            ),
            ImageDecodeCompatibility(
                assetName = MyImages.animWebp.fileName,
                size = Size(480, 270),
                minAPI = 26,
                inSampleSizeMinAPI = 26,
                inBitmapMinAPI = 26,
                inSampleSizeOnInBitmapMinAPI = 26,
            ),
            ImageDecodeCompatibility(
                assetName = MyImages.animHeif.fileName,
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
        val decodeWithInBitmap: (options: Options) -> Bitmap? = { options ->
            context.assets.open(image.assetName).use {
                BitmapFactory.decodeStream(it, null, options)
            }
        }
        val options = Options().apply {
            inSampleSize = sampleSize
        }
        val message =
            "enabledInBitmap=$enabledInBitmap, sampleSize=$sampleSize, sdk=${VERSION.SDK_INT}. $image"
        val extension = image.assetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"

        if (image.minAPI != -1 && VERSION.SDK_INT >= image.minAPI) {
            val sampledBitmapSize = calculateSampledBitmapSize(
                imageSize = image.size,
                sampleSize = options.inSampleSize,
                mimeType = mimeType
            )
            if (enabledInBitmap) {
                if (VERSION.SDK_INT >= image.inBitmapMinAPI) {
                    if (sampleSize > 1) {
                        options.inBitmap = Bitmap.createBitmap(
                            sampledBitmapSize.width,
                            sampledBitmapSize.height,
                            ARGB_8888
                        )
                        if (VERSION.SDK_INT >= image.inSampleSizeOnInBitmapMinAPI) {
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
                            if (GIF.matched(mimeType) && VERSION.SDK_INT == 19) {
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
                            ARGB_8888
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
                        ARGB_8888
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
                if (sampleSize > 1 && VERSION.SDK_INT >= image.inSampleSizeMinAPI) {
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
            if (bytes.isAnimatedWebP() && VERSION.SDK_INT == 17) {
                Assert.assertNotNull(message, bitmap)
            } else {
                Assert.assertNull(message, bitmap)
            }
        }
    }
}