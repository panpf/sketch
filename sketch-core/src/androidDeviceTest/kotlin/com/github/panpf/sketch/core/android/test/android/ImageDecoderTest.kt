package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.ImageDecoder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.android.test.android.Support.No
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.utils.ImageDecodeCompatibility
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.decodeImageUseImageDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.device.Devicex
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageDecoderTest {

    @Test
    fun testMutable() = runTest {
        if (VERSION.SDK_INT < 28) return@runTest
        val context = getTestContext()

        ComposeResImageFiles.jpeg.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertFalse(bitmap.isMutable)
            }

        ComposeResImageFiles.jpeg.toDataSource(context).decodeImageUseImageDecoder(mutable = true)
            .also { bitmap ->
                assertTrue(bitmap.isMutable)
            }
    }

    @Test
    fun testConfig() = runTest {
        if (VERSION.SDK_INT < 28 || VERSION.SDK_INT == VERSION_CODES.TIRAMISU) return@runTest
        val context = getTestContext()

        ComposeResImageFiles.jpeg.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertEquals(HARDWARE, bitmap.config)
            }

        ComposeResImageFiles.png.toDataSource(context).decodeImageUseImageDecoder().also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        ComposeResImageFiles.bmp.toDataSource(context).decodeImageUseImageDecoder().also { bitmap ->
            assertEquals(HARDWARE, bitmap.config)
        }

        ComposeResImageFiles.webp.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertEquals(HARDWARE, bitmap.config)
            }

        ComposeResImageFiles.heic.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertEquals(HARDWARE, bitmap.config)
            }

        ComposeResImageFiles.animGif.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertEquals(HARDWARE, bitmap.config)
            }

        ComposeResImageFiles.animWebp.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertEquals(HARDWARE, bitmap.config)
            }

        ComposeResImageFiles.animHeif.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertEquals(HARDWARE, bitmap.config)
            }
    }

    @Test
    fun testHasAlpha() = runTest {
        if (VERSION.SDK_INT < 28) return@runTest
        val context = getTestContext()

        ComposeResImageFiles.jpeg.toDataSource(context).decodeImageUseImageDecoder()
            .also { bitmap ->
                assertFalse(bitmap.hasAlpha())
            }

        ComposeResImageFiles.png.toDataSource(context).decodeImageUseImageDecoder().also { bitmap ->
            assertTrue(bitmap.hasAlpha())
        }
    }

    @Test
    fun testInSampleSize() = runTest {
        listOf(
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.jpeg,
                size = Size(1291, 1936),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.png,
                size = Size(750, 719),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.bmp,
                size = Size(700, 1012),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.webp,
                size = Size(1080, 1344),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.heic,
                size = Size(750, 932),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.animGif,
                size = Size(480, 480),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.animWebp,
                size = Size(480, 270),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
            ImageDecodeCompatibility(
                imageFile = ComposeResImageFiles.animHeif,
                size = Size(256, 144),
                minAPI = 28,
                inSampleSizeMinAPI = 28,
                inBitmapMinAPI = -1,
                inSampleSizeOnInBitmapMinAPI = -1
            ),
        ).forEach {
            if (!(it.imageFile.uri.contains("heic") || it.imageFile.uri.contains("heif")) && VERSION.SDK_INT == VERSION_CODES.TIRAMISU) {
                testDecodeImage(image = it, enabledInBitmap = false, sampleSize = 1)
                testDecodeImage(image = it, enabledInBitmap = false, sampleSize = 2)
                testDecodeImage(image = it, enabledInBitmap = true, sampleSize = 1)
                testDecodeImage(image = it, enabledInBitmap = true, sampleSize = 2)
            }
        }
    }

    @Test
    fun testFormat() = runTest {
        if (VERSION.SDK_INT < 28) return@runTest
        val context = getTestContext()
        listOf(
            // @formatter:off
            TestItem2(image = ComposeResImageFiles.jpeg, decodeAtLeast = 28, animated = No),
            TestItem2(image = ComposeResImageFiles.png, decodeAtLeast = 28, animated = No),
            TestItem2(image = ComposeResImageFiles.bmp, decodeAtLeast = 28, animated = No),
            TestItem2(image = ComposeResImageFiles.webp, decodeAtLeast = 28, animated = No),
            TestItem2(image = ComposeResImageFiles.heic, decodeAtLeast = 28, animated = No),
            TestItem2(image = ComposeResImageFiles.avif, decodeAtLeast = 31, animated = No),    // API 33 emulators decoding failed
            TestItem2(image = ComposeResImageFiles.animGif, atLeast = 28),
            TestItem2(image = ComposeResImageFiles.animWebp, atLeast = 28),
            TestItem2(image = ComposeResImageFiles.animHeif, decodeAtLeast = 28, animatedAtLeast = 30),    // API 33 emulators decoding failed
            TestItem2(image = ComposeResImageFiles.animAvif, atLeast = 31),
            // @formatter:on
        ).forEach {
            val data = it.image.toDataSource(context).toByteArray()
            val imageSource = ImageDecoder.createSource(ByteBuffer.wrap(data))
            // Android 13 (API level 33) emulator cannot decode images in AVIF and animated HEIF format; other versions work fine
            val expectedError = Devicex.isEmulator() && VERSION.SDK_INT == 33
                    && (it.image == ComposeResImageFiles.avif || it.image == ComposeResImageFiles.animHeif)
            if (it.decode.isSupport() && !expectedError) {
                var imageInfo1: ImageInfo? = null
                val drawable = try {
                    ImageDecoder.decodeDrawable(imageSource) { _, info, _ ->
                        imageInfo1 = ImageInfo(info.size.width, info.size.height, info.mimeType)
                    }
                } catch (e: Exception) {
                    throw Exception("Decode should succeed. ImageFile: ${it.image.name}", e)
                }
                val imageInfo = imageInfo1
                requireNotNull(imageInfo) {
                    "Decode callback ImageInfo should not be null. ImageFile: ${it.image.name}"
                }
                assertSizeEquals(
                    expected = it.image.size,
                    actual = imageInfo.size,
                    delta = Size(1, 1),
                    message = "Decode callback ImageInfo size should be equal to ImageFile size. ImageFile: ${it.image.name}"
                )
                assertEquals(
                    expected = it.image.mimeType,
                    actual = imageInfo.mimeType,
                    message = "Drawable mimeType should be equal to ImageFile mimeType. ImageFile: ${it.image.name}"
                )
                assertSizeEquals(
                    expected = it.image.size,
                    actual = drawable.intrinsicSize,
                    delta = Size(1, 1),
                    message = "Drawable size should be equal to ImageFile size. ImageFile: ${it.image.name}"
                )
                val animated = it.animated.isSupport()
                assertEquals(
                    expected = animated,
                    actual = drawable is android.graphics.drawable.Animatable,
                    message = "Drawable should be ${if (animated) "Animatable" else "Non Animatable"}. ImageFile: ${it.image.name}"
                )
            } else {
                assertFailsWith(
                    exceptionClass = Exception::class,
                    message = "Decode should fail. ImageFile: ${it.image.name}"
                ) {
                    ImageDecoder.decodeDrawable(imageSource)
                }
            }
        }
    }

    private suspend fun testDecodeImage(
        image: ImageDecodeCompatibility,
        enabledInBitmap: Boolean,
        sampleSize: Int
    ) {
        val context = getTestContext()
        val message = "enabledInBitmap=$enabledInBitmap, sampleSize=$sampleSize. $image"
        val extension = image.imageFile.uri.substringAfterLast('.', missingDelimiterValue = "")
        val mimeType = "image/$extension"
        val imageSize = image.size

        if (VERSION.SDK_INT >= image.minAPI) {
            try {
                image.imageFile.toDataSource(context).decodeImageUseImageDecoder(sampleSize)
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
                val byteArray = image.imageFile.toDataSource(context).toByteArray()
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(ByteBuffer.wrap(byteArray))
                )
            }
        }
    }
}