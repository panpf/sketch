package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newBitmapRegionDecoderInstanceCompat
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class BitmapRegionDecoderTest {

    @Test
    fun testMutable() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName
        val imageSize = Size(1291, 1936)

        val options = Options()
        assertFalse(options.inMutable)
        val bitmap = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertFalse(bitmap.isMutable)

        options.inMutable = true
        assertTrue(options.inMutable)
        val bitmap1 = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertFalse(bitmap1.isMutable)
    }

    @Test
    fun testInPreferredConfig() {
        val context = getTestContext()
        val imageName = ResourceImages.jpeg.resourceName
        val imageSize = Size(1291, 1936)

        val options = Options()
        assertEquals(ARGB_8888, options.inPreferredConfig)
        val bitmap = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertEquals(ARGB_8888, bitmap.config)

        @Suppress("DEPRECATION")
        options.inPreferredConfig = ARGB_4444
        @Suppress("DEPRECATION")
        assertEquals(ARGB_4444, options.inPreferredConfig)
        val bitmap1 = context.assets.open(imageName)
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        if (VERSION.SDK_INT > VERSION_CODES.M) {
            assertEquals(ARGB_8888, bitmap1.config)
        } else {
            @Suppress("DEPRECATION")
            assertEquals(ARGB_4444, bitmap1.config)
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
                inSampleSizeOnInBitmapMinAPI = 16,
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.png.resourceName,
                size = Size(750, 719),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inSampleSizeOnInBitmapMinAPI = 16,
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.bmp.resourceName,
                size = Size(700, 1012),
                minAPI = -1,
                inSampleSizeMinAPI = -1,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1,
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.webp.resourceName,
                size = Size(1080, 1344),
                minAPI = 16,
                inSampleSizeMinAPI = 16,
                inBitmapMinAPI = 16,
                inSampleSizeOnInBitmapMinAPI = 16,
            ),
            com.github.panpf.sketch.test.utils.ImageDecodeCompatibility(
                assetName = ResourceImages.heic.resourceName,
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = 28,
                inSampleSizeOnInBitmapMinAPI = 28,
            ),
        ).forEach {
            val itemWidth = it.size.width / 3
            val itemHeight = it.size.height / 3
            val regionRect =
                Rect(itemWidth, itemHeight, itemWidth + itemWidth, itemHeight + itemHeight).apply {
                    if (width() % 2 == 0) right++
                    if (height() % 2 == 0) bottom++
                }
            val fullRect = Rect(0, 0, it.size.width, it.size.height)
            testRegionDecodeImage(
                image = it,
                regionRect = regionRect,
                enabledInBitmap = false,
                sampleSize = 1
            )
            testRegionDecodeImage(
                image = it,
                regionRect = fullRect,
                enabledInBitmap = false,
                sampleSize = 1
            )
            testRegionDecodeImage(
                image = it,
                regionRect = regionRect,
                enabledInBitmap = false,
                sampleSize = 2
            )
            testRegionDecodeImage(
                image = it,
                regionRect = fullRect,
                enabledInBitmap = false,
                sampleSize = 2
            )
            testRegionDecodeImage(
                image = it,
                regionRect = regionRect,
                enabledInBitmap = true,
                sampleSize = 1
            )
            testRegionDecodeImage(
                image = it,
                regionRect = fullRect,
                enabledInBitmap = true,
                sampleSize = 1
            )
            testRegionDecodeImage(
                image = it,
                regionRect = regionRect,
                enabledInBitmap = true,
                sampleSize = 2
            )
            testRegionDecodeImage(
                image = it,
                regionRect = fullRect,
                enabledInBitmap = true,
                sampleSize = 2
            )
        }
    }

    private fun testRegionDecodeImage(
        image: com.github.panpf.sketch.test.utils.ImageDecodeCompatibility,
        regionRect: Rect,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        val context = getTestContext()
        val decodeWithInBitmap: (options: Options) -> Bitmap? = { options ->
            context.assets.open(image.assetName).use {
                it.newBitmapRegionDecoderInstanceCompat()!!.decodeRegion(regionRect, options)
            }
        }
        val options = Options().apply {
            inSampleSize = sampleSize
        }
        val message =
            "enabledInBitmap=$enabledInBitmap, sampleSize=$sampleSize, sdk=${VERSION.SDK_INT}, regionRect=$regionRect. $image"
        val extension = image.assetName.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"

        if (image.minAPI != -1 && VERSION.SDK_INT >= image.minAPI) {
            val sampledBitmapSize = calculateSampledBitmapSizeForRegion(
                regionSize = Size(regionRect.width(), regionRect.height()),
                sampleSize = options.inSampleSize,
                mimeType = mimeType,
                imageSize = image.size
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
                            try {
                                val bitmap = decodeWithInBitmap(options)!!
                                fail("inBitmapAndInSampleSizeMinAPI error. bitmap=${bitmap.toShortInfoString()}. $message")
                            } catch (e: IllegalArgumentException) {
                                if (e.message != "Problem decoding into existing bitmap") {
                                    throw Exception("exception type error. $message", e)
                                }
                            }
                        }
                    } else {
                        /* sampleSize 1 */
                        options.inBitmap = Bitmap.createBitmap(
                            regionRect.width(),
                            regionRect.height(),
                            ARGB_8888
                        )
                        try {
                            decodeWithInBitmap(options)!!
                        } catch (e: IllegalArgumentException) {
                            throw Exception(message, e)
                        }.also { bitmap ->
                            assertSame(options.inBitmap, bitmap, message)
                            assertEquals(regionRect.size(), bitmap.size, message)
                        }
                    }
                } else {
                    /* inBitmapMinAPI not support */
                    options.inBitmap = Bitmap.createBitmap(
                        regionRect.width(),
                        regionRect.height(),
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
                    assertEquals(regionRect.size(), bitmap.size, message)
                }
            }
        } else {
            /* minAPI not support */
            assertThrow(IOException::class) {
                decodeWithInBitmap(options)
            }
        }
    }

    private fun <R> BitmapRegionDecoder.use(block: BitmapRegionDecoder.() -> R): R {
        try {
            return block(this)
        } finally {
            recycle()
        }
    }

    private fun Rect.size(): Size = Size(width(), height())
}