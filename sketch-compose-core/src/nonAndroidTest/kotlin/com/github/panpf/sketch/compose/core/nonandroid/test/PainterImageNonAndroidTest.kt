package com.github.panpf.sketch.compose.core.nonandroid.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.painter.AnimatedImagePainter
import com.github.panpf.sketch.painter.ImageBitmapPainter
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.SketchSize
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class PainterImageNonAndroidTest {

    @Test
    fun testImageAsPainter() {
        val colorPainter = ColorPainter(Color.Green)

        assertSame(
            expected = colorPainter,
            actual = colorPainter.asImage().asPainter()
        )

        val bitmap = createBitmap(100, 100)
        assertEquals(
            expected = FilterQuality.Low,
            actual = bitmap.asImage().asPainter().asOrThrow<ImageBitmapPainter>().filterQuality
        )
        assertEquals(
            expected = FilterQuality.High,
            actual = bitmap.asImage().asPainter(FilterQuality.High)
                .asOrThrow<ImageBitmapPainter>().filterQuality
        )

        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val animatedImage = AnimatedImage(codec)
        assertEquals(
            expected = AnimatedImagePainter(animatedImage),
            actual = animatedImage.asPainter()
        )

        assertFailsWith(IllegalArgumentException::class) {
            FakeImage(SketchSize(100, 100)).asPainter()
        }
    }
}