package com.github.panpf.sketch.video.test.decode.internal

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
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRect
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VideoFrameDecodeHelperTest {

    @Test
    fun testDecode() {
        if (VERSION.SDK_INT < VERSION_CODES.O_MR1) return
        val (context, sketch) = getTestContextAndSketch()

        /*
         * config: sampleSize
         */
        val imageFile = ResourceImages.mp4
        imageFile.toDecodeHelper(context, sketch)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
            }
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

        /*
         * config: colorType
         */
        imageFile.toDecodeHelper(context, sketch)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                val expected =
                    if (VERSION.SDK_INT >= VERSION_CODES.R) ColorType.ARGB_8888 else ColorType.RGB_565
                assertEquals(
                    expected = expected,
                    actual = colorType,
                )
            }
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

        /*
         * config: colorSpace
         */
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            imageFile.toDecodeHelper(context, sketch)
                .decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
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

        /*
         * error
         */
        // RuntimeException: setDataSource failed
        assertFailsWith(RuntimeException::class) {
            ResourceImages.svg.toDecodeHelper(context, sketch).decode(sampleSize = 1)
        }
    }

    @Test
    fun testDecodeRegion() {
        if (VERSION.SDK_INT < VERSION_CODES.O_MR1) return
        val (context, sketch) = getTestContextAndSketch()

        val imageFile = ResourceImages.mp4
        assertFailsWith(UnsupportedOperationException::class) {
            imageFile.toDecodeHelper(context, sketch)
                .decodeRegion(
                    region = imageFile.size.toRect(),
                    sampleSize = 1
                )
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        val imageFile = ResourceImages.mp4
        val request = ImageRequest(context, imageFile.uri)
        val dataSource = imageFile.toDataSource(context)
        val decodeHelper = VideoFrameDecodeHelper(sketch, request, dataSource, imageFile.mimeType)
        assertEquals(
            expected = "VideoFrameDecodeHelper(request=$request, dataSource=$dataSource)",
            actual = decodeHelper.toString()
        )
    }

    private fun ResourceImageFile.toDecodeHelper(
        context: PlatformContext,
        sketch: Sketch,
        dataSource: DataSource? = null,
        configBlock: (ImageRequest.Builder.() -> Unit)? = null
    ): VideoFrameDecodeHelper {
        return VideoFrameDecodeHelper(
            sketch = sketch,
            request = ImageRequest(context, uri, configBlock),
            dataSource = dataSource ?: toDataSource(context),
            mimeType = mimeType
        )
    }
}