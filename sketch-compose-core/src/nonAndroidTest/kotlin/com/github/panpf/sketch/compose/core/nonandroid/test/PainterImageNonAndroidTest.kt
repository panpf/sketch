package com.github.panpf.sketch.compose.core.nonandroid.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.painter.ComposeBitmapPainter
import com.github.panpf.sketch.painter.SkiaAnimatedImagePainter
import com.github.panpf.sketch.test.utils.FakeImage
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
import kotlin.test.assertTrue

class PainterImageNonAndroidTest {

    @Test
    fun testImageAsPainter() {
        val colorPainter = ColorPainter(Color.Green)

        assertSame(
            expected = colorPainter,
            actual = colorPainter.asImage().asPainter()
        )

        val bitmap = SkiaBitmap(100, 100)
        assertTrue(
            actual = bitmap.asImage().asPainter() is ComposeBitmapPainter
        )

        val context = getTestContext()
        val codec = ResourceImages.animGif.toDataSource(context)
            .openSource().buffer().use { it.readByteArray() }
            .let { Data.makeFromBytes(it) }
            .let { Codec.makeFromData(it) }
        val skiaAnimatedImage = SkiaAnimatedImage(codec)
        assertEquals(
            expected = SkiaAnimatedImagePainter(skiaAnimatedImage),
            actual = skiaAnimatedImage.asPainter()
        )

        assertFailsWith(IllegalArgumentException::class) {
            FakeImage(SketchSize(100, 100)).asPainter()
        }
    }
}