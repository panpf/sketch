package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ImageFormat.GIF
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class BitmapFactoryTest {

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName

        val options = Options()
        assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertFalse(bitmap.isMutable)

        options.inMutable = true
        assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertTrue(bitmap1.isMutable)
    }

    @Test
    fun testInPreferredConfig() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName

        val bitmap0 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!
        assertEquals(ARGB_8888, bitmap0.config)

        val options = Options()
        assertEquals(ARGB_8888, options.inPreferredConfig)
        val bitmap = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertEquals(ARGB_8888, bitmap.config)

        options.inPreferredConfig = ARGB_4444
        assertEquals(ARGB_4444, options.inPreferredConfig)
        val bitmap1 = context.assets.open(imageName).use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        if (VERSION.SDK_INT > VERSION_CODES.M) {
            assertEquals(ARGB_8888, bitmap1.config)
        } else {
            assertEquals(ARGB_4444, bitmap1.config)
        }
    }

    @Test
    fun testHasAlpha() {
        val context = getTestContext()

        context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            assertEquals(ARGB_8888, config)
            assertFalse(hasAlpha())
        }

        context.assets.open(ResourceImages.png.resourceName).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            assertEquals(ARGB_8888, config)
            assertTrue(hasAlpha())
        }
    }

    @Test
    fun testInBitmapAndInSampleSize() {
        listOf(
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.jpeg.resourceName,
                size = Size(1291, 1936),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inSampleSizeOnInBitmapMinAPI = 19
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.png.resourceName,
                size = Size(750, 719),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inSampleSizeOnInBitmapMinAPI = 19
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.bmp.resourceName,
                size = Size(700, 1012),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 19,
                inSampleSizeOnInBitmapMinAPI = 19,
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.webp.resourceName,
                size = Size(1080, 1344),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 19,
                inSampleSizeOnInBitmapMinAPI = 19,
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.heic.resourceName,
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
        image: com.github.panpf.sketch.test.utils.ImageDecodeCompatibility,
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
                                assertSame(options.inBitmap, bitmap, message)
                                assertEquals(sampledBitmapSize, bitmap.size, message)
                            }
                        } else {
                            /* sampleSize not support */
                            if (GIF.matched(mimeType) && VERSION.SDK_INT == 19) {
                                try {
                                    decodeWithInBitmap(options)!!
                                } catch (e: IllegalArgumentException) {
                                    throw Exception(message, e)
                                }.also { bitmap ->
                                    assertSame(options.inBitmap, bitmap, message)
                                    assertEquals(image.size, bitmap.size, message)
                                }
                            } else {
                                try {
                                    val bitmap = decodeWithInBitmap(options)!!
                                    fail("inBitmapAndInSampleSizeMinAPI error. bitmap=${bitmap.toShortInfoString()}. $message")
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
                            assertSame(options.inBitmap, bitmap, message)
                            assertEquals(image.size, bitmap.size, message)
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
                        fail("inBitmapMinAPI error. bitmap=${bitmap.toShortInfoString()}. $message")
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
                    assertEquals(sampledBitmapSize, bitmap.size, message)
                } else {
                    assertEquals(image.size, bitmap.size, message)
                }
            }
        } else {
            /* minAPI not support */
            val bitmap = try {
                decodeWithInBitmap(options)
            } catch (e: IllegalArgumentException) {
                throw Exception(message, e)
            }
            assertNull(bitmap, message)
        }
    }
}