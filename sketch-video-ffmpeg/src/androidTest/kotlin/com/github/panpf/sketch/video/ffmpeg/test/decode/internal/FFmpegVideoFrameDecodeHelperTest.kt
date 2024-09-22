package com.github.panpf.sketch.video.ffmpeg.test.decode.internal

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecodeHelper
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
import com.github.panpf.tools4a.device.Devicex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FFmpegVideoFrameDecodeHelperTest {

    @Test
    fun testDecode() {
        if (VERSION.SDK_INT < 24 && Devicex.isEmulator()) {
            // UnsatisfiedLinkError /data/app/com.github.panpf.sketch.video.ffmpeg.test-1/lib/arm64/libssl.so
            return
        }

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
                    if (VERSION.SDK_INT >= VERSION_CODES.N) ColorType.ARGB_8888 else ColorType.RGB_565
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
        // ImageInvalidException: Invalid video file
        assertFailsWith(ImageInvalidException::class) {
            ResourceImages.svg.toDecodeHelper(context, sketch).decode(sampleSize = 1)
        }
    }

    @Test
    fun testDecodeRegion() {
        if (VERSION.SDK_INT < 24 && Devicex.isEmulator()) {
            // UnsatisfiedLinkError /data/app/com.github.panpf.sketch.video.ffmpeg.test-1/lib/arm64/libssl.so
            return
        }

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
        val decodeHelper =
            FFmpegVideoFrameDecodeHelper(sketch, request, dataSource, imageFile.mimeType)
        assertEquals(
            expected = "FFmpegVideoFrameDecodeHelper(request=$request, dataSource=$dataSource, mimeType=${imageFile.mimeType})",
            actual = decodeHelper.toString()
        )
    }

    private fun ResourceImageFile.toDecodeHelper(
        context: PlatformContext,
        sketch: Sketch,
        dataSource: DataSource? = null,
        configBlock: (ImageRequest.Builder.() -> Unit)? = null
    ): FFmpegVideoFrameDecodeHelper {
        return FFmpegVideoFrameDecodeHelper(
            sketch = sketch,
            request = ImageRequest(context, uri, configBlock),
            dataSource = dataSource ?: toDataSource(context),
            mimeType = mimeType
        )
    }
}