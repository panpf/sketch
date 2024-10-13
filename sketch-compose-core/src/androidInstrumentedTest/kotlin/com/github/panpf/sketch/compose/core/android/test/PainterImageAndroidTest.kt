package com.github.panpf.sketch.compose.core.android.test

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.painter.DrawableAnimatablePainter
import com.github.panpf.sketch.painter.DrawablePainter
import com.github.panpf.sketch.painter.ImageBitmapPainter
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PainterImageAndroidTest {

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

        val animatableDrawable = TestAnimatableDrawable(ColorDrawable(TestColor.RED))
        assertEquals(
            expected = DrawableAnimatablePainter(animatableDrawable),
            actual = animatableDrawable.asImage().asPainter()
        )

        val bitmapDrawable = BitmapDrawable(null, bitmap)
        assertTrue(
            actual = bitmapDrawable.asImage().asPainter() is BitmapPainter,
        )

        val colorDrawable = ColorDrawable(TestColor.RED)
        assertEquals(
            expected = ColorPainter(Color(TestColor.RED)),
            actual = colorDrawable.asImage().asPainter()
        )

        val layerDrawable = LayerDrawable(arrayOf())
        assertEquals(
            expected = DrawablePainter(layerDrawable),
            actual = layerDrawable.asImage().asPainter()
        )

        assertFailsWith(IllegalArgumentException::class) {
            FakeImage(SketchSize(100, 100)).asPainter()
        }
    }
}