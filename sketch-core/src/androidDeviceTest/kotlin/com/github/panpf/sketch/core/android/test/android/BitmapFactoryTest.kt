package com.github.panpf.sketch.core.android.test.android

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.android.test.android.Support.Ok
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.device.Devicex
import kotlinx.coroutines.test.runTest
import okio.buffer
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapFactoryTest {

    @Test
    fun testMutable() = runTest {
        val context = getTestContext()
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)

        val options = Options()
        assertFalse(options.inMutable)
        val bitmap = dataSource.openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertFalse(bitmap.isMutable)

        options.inMutable = true
        assertTrue(options.inMutable)
        val bitmap1 = dataSource.openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertTrue(bitmap1.isMutable)
    }

    @Test
    fun testInPreferredConfig() = runTest {
        val context = getTestContext()
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)

        val bitmap0 = dataSource.openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, null)
        }!!
        assertEquals(ARGB_8888, bitmap0.config)

        val options = Options()
        assertEquals(ARGB_8888, options.inPreferredConfig)
        val bitmap = dataSource.openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        assertEquals(ARGB_8888, bitmap.config)

        options.inPreferredConfig = ARGB_4444
        assertEquals(ARGB_4444, options.inPreferredConfig)
        val bitmap1 = dataSource.openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, options)
        }!!
        if (VERSION.SDK_INT > VERSION_CODES.M) {
            assertEquals(ARGB_8888, bitmap1.config)
        } else {
            assertEquals(ARGB_4444, bitmap1.config)
        }
    }

    @Test
    fun testHasAlpha() = runTest {
        val context = getTestContext()

        ComposeResImageFiles.jpeg.toDataSource(context).openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            assertEquals(ARGB_8888, config)
            assertFalse(hasAlpha())
        }

        ComposeResImageFiles.png.toDataSource(context).openSource().buffer().inputStream().use {
            BitmapFactory.decodeStream(it, null, null)
        }!!.apply {
            assertEquals(ARGB_8888, config)
            assertTrue(hasAlpha())
        }
    }

    @Test
    fun testFormat() = runTest {
        val context = getTestContext()
        listOf(
            // @formatter:off
            TestItem(image = ComposeResImageFiles.jpeg, support = Ok),
            TestItem(image = ComposeResImageFiles.png, support = Ok),
            TestItem(image = ComposeResImageFiles.bmp, support = Ok),
            TestItem(image = ComposeResImageFiles.webp, support = Ok),
            TestItem(image = ComposeResImageFiles.heic, atLeast = 27),
            TestItem(image = ComposeResImageFiles.avif, atLeast = 31),  // API 33 emulators decoding failed
            TestItem(image = ComposeResImageFiles.animGif, support = Ok),
            TestItem(image = ComposeResImageFiles.animWebp, bounds = Ok, decodeAtLeast = 26),
            TestItem(image = ComposeResImageFiles.animHeif, atLeast = 27),  // API 33 emulators decoding failed
            TestItem(image = ComposeResImageFiles.animAvif, boundsAtLeast = 29, decodeAtLeast = 31),    // API 29 and 30 mimeType is "image/heif"
            // @formatter:on
        ).forEach {
            val data = it.image.toDataSource(context).toByteArray()

            val options = Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            if (it.bounds.isSupport()) {
                assertSizeEquals(
                    expected = it.image.size,
                    actual = Size(width = options.outWidth, height = options.outHeight),
                    delta = Size(1, 1),
                    message = "Decode size should same as image size. ImageFile: ${it.image.name}"
                )
                val correctedMimeType = if (it.image == ComposeResImageFiles.animAvif
                    && VERSION.SDK_INT >= 29
                    && VERSION.SDK_INT <= 30
                ) {
                    "image/heif"    // Android 10 and 11 decode avif format as heif, but Android 12 and above decode avif format as avif
                } else {
                    it.image.mimeType
                }
                assertEquals(
                    expected = correctedMimeType,
                    actual = options.outMimeType,
                    message = "Decode mimeType should same as image mimeType. ImageFile: ${it.image.name}"
                )
            } else {
                assertEquals(
                    expected = ImageInfo(-1, -1, ""),
                    actual = ImageInfo(
                        width = options.outWidth,
                        height = options.outHeight,
                        mimeType = options.outMimeType.orEmpty()
                    ),
                    message = "Decode should fail. ImageFile: ${it.image.name}"
                )
            }

            if (it.decode.isSupport()) {
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                // BitmapFactory on Android 13 (API level 33) emulator cannot decode images in AVIF and animated HEIF format; other versions work fine
                val expectedError = Devicex.isEmulator() && VERSION.SDK_INT == 33
                        && (it.image == ComposeResImageFiles.avif || it.image == ComposeResImageFiles.animHeif)
                if (!expectedError) {
                    assertNotNull(
                        actual = bitmap,
                        message = "Decode should succeed. ImageFile: ${it.image.name}"
                    )

                    assertSizeEquals(
                        expected = it.image.size,
                        actual = bitmap.size,
                        delta = Size(1, 1),
                        message = "Bitmap size should same as image size. ImageFile: ${it.image.name}"
                    )
                } else {
                    assertNull(
                        actual = bitmap,
                        message = "Decode should fail. ImageFile: ${it.image.name}"
                    )
                }
            } else {
                assertNull(
                    actual = BitmapFactory.decodeByteArray(data, 0, data.size),
                    message = "Decode should fail. ImageFile: ${it.image.name}"
                )
            }
        }
    }
}