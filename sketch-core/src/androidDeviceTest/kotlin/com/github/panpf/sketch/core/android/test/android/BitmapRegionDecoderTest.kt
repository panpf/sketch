package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.android.test.android.Support.No
import com.github.panpf.sketch.core.android.test.android.Support.Ok
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newBitmapRegionDecoderInstanceCompat
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.div
import com.github.panpf.tools4a.device.Devicex
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

@RunWith(AndroidJUnit4::class)
class BitmapRegionDecoderTest {

    @Test
    fun testMutable() = runTest {
        val context = getTestContext()
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)
        val imageSize = Size(1291, 1936)

        val options = Options()
        assertFalse(options.inMutable)
        val bitmap = ByteArrayInputStream(dataSource.toByteArray())
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertFalse(bitmap.isMutable)

        options.inMutable = true
        val bitmap1 = ByteArrayInputStream(dataSource.toByteArray())
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertFalse(bitmap1.isMutable)
    }

    @Test
    fun testInPreferredConfig() = runTest {
        val context = getTestContext()
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)
        val imageSize = Size(1291, 1936)

        val options = Options()
        assertEquals(ARGB_8888, options.inPreferredConfig)
        val bitmap = ByteArrayInputStream(dataSource.toByteArray())
            .run { newBitmapRegionDecoderInstanceCompat() }!!
            .use { decodeRegion(Rect(0, 0, imageSize.width, imageSize.height), options) }!!
        assertEquals(ARGB_8888, bitmap.config)

        @Suppress("DEPRECATION")
        options.inPreferredConfig = ARGB_4444
        @Suppress("DEPRECATION")
        assertEquals(ARGB_4444, options.inPreferredConfig)
        val bitmap1 = ByteArrayInputStream(dataSource.toByteArray())
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
    fun testFormat() = runTest {
        val context = getTestContext()
        listOf(
            // @formatter:off
            TestItem(image = ComposeResImageFiles.jpeg, support = Ok),
            TestItem(image = ComposeResImageFiles.png, support = Ok),
            TestItem(image = ComposeResImageFiles.bmp, support = No),
            TestItem(image = ComposeResImageFiles.webp, support = Ok),
            TestItem(image = ComposeResImageFiles.heic, atLeast = 27),  // API 36 emulators decoding regions failed
            TestItem(image = ComposeResImageFiles.avif, atLeast = 37),
            TestItem(image = ComposeResImageFiles.animGif, support = No),
            TestItem(image = ComposeResImageFiles.animWebp, bounds = Ok, decodeAtLeast = 26),
            TestItem(image = ComposeResImageFiles.animHeif, atLeast = 27),  // API 33 emulators decoding failed
            TestItem(image = ComposeResImageFiles.animAvif, atLeast = 37),  // API 29-30 only support decoding bounds
            // @formatter:on
        ).forEach {
            val data = it.image.toDataSource(context).toByteArray()

            // Android 11 and Android 12 support decoding animated AVIF,
            // but do not support BitmapRegionDecoder to decode animated AVIF,
            // so it is temporarily considered to support BitmapRegionDecoder to decode animated AVIF on Android 11 and Android 12.
            val tempSupportBounds = it.image == ComposeResImageFiles.animAvif
                    && VERSION.SDK_INT >= 29
                    && VERSION.SDK_INT <= 30
            if (it.bounds.isSupport() || tempSupportBounds) {
                val decoder = try {
                    data.inputStream().newBitmapRegionDecoderInstanceCompat()!!
                } catch (e: Exception) {
                    throw Exception(
                        "Create BitmapRegionDecoder should succeed. ImageFile: ${it.image.name}",
                        e
                    )
                }
                assertSizeEquals(
                    expected = it.image.size,
                    actual = Size(decoder.width, decoder.height),
                    delta = Size(1, 1),
                    message = "Decoder size should be same as image size. ImageFile: ${it.image.name}"
                )
            } else {
                assertFailsWith(
                    exceptionClass = Exception::class,
                    message = "Create BitmapRegionDecoder should fail. ImageFile: ${it.image.name}"
                ) {
                    data.inputStream().newBitmapRegionDecoderInstanceCompat()
                }
            }

            val halfRegionSize = it.image.size / 2f
            val halfRegionLeft = (it.image.size.width - halfRegionSize.width) / 2
            val halfRegionTop = (it.image.size.height - halfRegionSize.height) / 2
            val halfRegion = Rect(
                /* left = */ halfRegionLeft,
                /* top = */ halfRegionTop,
                /* right = */ halfRegionLeft + halfRegionSize.width,
                /* bottom = */ halfRegionTop + halfRegionSize.height
            )
            val halfOptions = Options().apply { inSampleSize = 2 }
            if (it.decode.isSupport()) {
                val decoder = try {
                    data.inputStream().newBitmapRegionDecoderInstanceCompat()!!
                } catch (e: Exception) {
                    throw Exception(
                        "Create BitmapRegionDecoder should succeed. ImageFile: ${it.image.name}",
                        e
                    )
                }
                try {
                    val originRegion = Rect(0, 0, decoder.width, decoder.height)
                    val originOptions = Options().apply { inSampleSize = 1 }

                    // BitmapRegionDecoder on Android 13 (API level 33) emulator cannot decode images in AVIF format; other versions work fine
                    val expectedError = Devicex.isEmulator() && VERSION.SDK_INT == 33
                            && it.image == ComposeResImageFiles.animHeif
                    val bitmap1 = decoder.decodeRegion(originRegion, originOptions)
                    val bitmap2 = decoder.decodeRegion(originRegion, halfOptions)
                    if (!expectedError) {
                        require(bitmap1 != null && bitmap2 != null) {
                            "All decode region result must not be null. " +
                                    "bitmap1=${bitmap1?.size}, " +
                                    "bitmap2=${bitmap2?.size}, " +
                                    "ImageFile: ${it.image.name}"
                        }

                        assertSizeEquals(
                            expected = it.image.size,
                            actual = bitmap1.size,
                            delta = Size(1, 1),
                            message = "Decode bitmap size should be same as image size. ImageFile: ${it.image.name}"
                        )
                        assertSizeEquals(
                            expected = it.image.size / 2f,
                            actual = bitmap2.size,
                            delta = Size(1, 1),
                            message = "Decode bitmap size should be half of image size. ImageFile: ${it.image.name}"
                        )
                    } else {
                        require(bitmap1 == null && bitmap2 == null) {
                            "All decode region result must be null. " +
                                    "bitmap1=${bitmap1?.size}, " +
                                    "bitmap2=${bitmap2?.size}, " +
                                    "ImageFile: ${it.image.name}"
                        }
                    }

                    val notSupportRegion = Devicex.isEmulator() && VERSION.SDK_INT == 36
                            && it.image == ComposeResImageFiles.heic
                    val bitmap3 = decoder.decodeRegion(halfRegion, originOptions)
                    val bitmap4 = decoder.decodeRegion(halfRegion, halfOptions)
                    if (!expectedError && !notSupportRegion) {
                        require(bitmap3 != null && bitmap4 != null) {
                            "All decode region result must not be null. " +
                                    "bitmap3=${bitmap3?.size}, " +
                                    "bitmap4=${bitmap4?.size}. " +
                                    "ImageFile: ${it.image.name}, " +
                                    "halfRegion=${halfRegion.toShortString()}"
                        }

                        assertSizeEquals(
                            expected = halfRegionSize,
                            actual = bitmap3.size,
                            delta = Size(1, 1),
                            message = "Decode region bitmap size should be same as region size. ImageFile: ${it.image.name}"
                        )
                        assertSizeEquals(
                            expected = halfRegionSize / 2f,
                            actual = bitmap4.size,
                            delta = Size(1, 1),
                            message = "Decode region bitmap size should be half of region size. ImageFile: ${it.image.name}"
                        )
                    } else {
                        require(bitmap3 == null && bitmap4 == null) {
                            "All decode region result must be null. " +
                                    "bitmap3=${bitmap2?.size}, " +
                                    "bitmap4=${bitmap4?.size}. " +
                                    "ImageFile: ${it.image.name}"
                        }
                    }
                } finally {
                    decoder.recycle()
                }
            } else {
                assertFailsWith(
                    exceptionClass = Exception::class,
                    message = "Decode region should fail. ImageFile: ${it.image.name}"
                ) {
                    val decoder =
                        requireNotNull(data.inputStream().newBitmapRegionDecoderInstanceCompat()) {
                            "Create BitmapRegionDecoder failed."
                        }
                    try {
                        requireNotNull(decoder.decodeRegion(halfRegion, halfOptions)) {
                            "Decode region failed."
                        }
                    } finally {
                        decoder.recycle()
                    }
                }
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
}