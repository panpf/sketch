package com.github.panpf.sketch.video.android.test.decode.internal

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.internal.VideoFrameDecodeHelper
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.toRect
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VideoFrameDecodeHelperTest {

    @Test
    fun testDecode() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.O_MR1) return@runTest
        val (context, sketch) = getTestContextAndSketch()

        // normal
        val imageFile = ComposeResImageFiles.rotationMp4
        imageFile.toDecodeHelper(context, sketch)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
                val expectedColorType = if (VERSION.SDK_INT >= VERSION_CODES.R)
                    ColorType.ARGB_8888 else ColorType.RGB_565
                assertEquals(
                    expected = expectedColorType,
                    actual = colorType,
                )
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.SRGB),
                    actual = colorSpace,
                )
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
            }

        // sampleSize
        imageFile.toDecodeHelper(context, sketch)
            .decode(sampleSize = 2)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = calculateSampledBitmapSize(imageFile.size, 2),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        // colorType
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            imageFile.toDecodeHelper(context, sketch) {
                colorType(ColorType.RGBA_F16)
            }.decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorType.ARGB_8888,
                        actual = colorType,
                    )
                }
        }

        // colorSpace
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            imageFile.toDecodeHelper(context, sketch) {
                colorSpace(ColorSpace.Named.DISPLAY_P3)
            }.decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
        }

        // preferVideoCover
        imageFile.toDecodeHelper(context, sketch) {
            preferVideoCover()
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = Size(1600, 1200),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        /*
         * error
         */
        // RuntimeException: setDataSource failed
        assertFailsWith(RuntimeException::class) {
            ComposeResImageFiles.svg.toDecodeHelper(context, sketch).decode(sampleSize = 1)
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.O_MR1) return@runTest
        val (context, sketch) = getTestContextAndSketch()

        val imageFile = ComposeResImageFiles.rotationMp4
        assertFailsWith(UnsupportedOperationException::class) {
            imageFile.toDecodeHelper(context, sketch)
                .decodeRegion(
                    region = imageFile.size.toRect(),
                    sampleSize = 1
                )
        }

        imageFile.toDecodeHelper(context, sketch) {
            preferVideoCover()
        }.decodeRegion(
            region = Rect(100, 100, 300, 200),
            sampleSize = 1
        ).apply {
            assertSizeEquals(
                expected = Size(200, 100),
                actual = size,
                delta = Size(1, 1)
            )
        }
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageFile = ComposeResImageFiles.mp4
        val request = ImageRequest(context, imageFile.uri)
        val dataSource = imageFile.toDataSource(context)
        val decodeHelper = VideoFrameDecodeHelper(sketch, request, dataSource, imageFile.mimeType)
        assertEquals(
            expected = "VideoFrameDecodeHelper(request=$request, dataSource=$dataSource, mimeType=${imageFile.mimeType})",
            actual = decodeHelper.toString()
        )
    }

    private suspend fun ComposeResImageFile.toDecodeHelper(
        context: PlatformContext,
        sketch: Sketch,
        dataSource: DataSource? = null,
        block: (ImageRequest.Builder.() -> Unit)? = null
    ): VideoFrameDecodeHelper {
        return VideoFrameDecodeHelper(
            sketch = sketch,
            request = ImageRequest(context, uri, block),
            dataSource = dataSource ?: toDataSource(context),
            mimeType = mimeType
        )
    }
}