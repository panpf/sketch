package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastRoundToInt
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.ui.util.BackgroundPainter
import com.github.panpf.sketch.sample.ui.util.SizeColorPainter
import com.github.panpf.sketch.sample.ui.util.scale
import com.github.panpf.sketch.transition.CrossfadeTransition

class CrossfadePainterTestScreen : BasePainterTestScreen() {

    @Composable
    override fun getTitle(): String = "CrossfadePainter"

    override suspend fun buildPainters(
        context: PlatformContext,
        contentScale: ContentScale,
        alignment: Alignment,
        itemWidth: Float,
    ): List<Pair<String, Painter>> {
        val startPainterWidth = itemWidth * 0.75f
        val startDrawableHeight = itemWidth * 0.65f
        val startPainter = SizeColorPainter(
            color = Color.Blue,
            size = Size(startPainterWidth, startDrawableHeight)
        )
        val bitmap = Res.readBytes("drawable/numbers.jpg").decodeToImageBitmap()
        val endPainterWidth = itemWidth * 0.5f
        val endImageBitmap = bitmap.scale(
            size = IntSize(
                width = endPainterWidth.fastRoundToInt(),
                height = endPainterWidth.fastRoundToInt()
            )
        )
        val endPainter = BitmapPainter(endImageBitmap)
        return mutableListOf<Pair<String, Painter>>(
            "Default" to CrossfadePainter(
                start = startPainter,
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
            ),
            "Long Duration" to CrossfadePainter(
                start = startPainter,
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
                durationMillis = CrossfadeTransition.DEFAULT_DURATION_MILLIS * 4
            ),
            "No fadeStart" to CrossfadePainter(
                start = startPainter,
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
                fadeStart = !CrossfadeTransition.DEFAULT_FADE_START
            ),
            "PreferExactIntrinsicSize" to CrossfadePainter(
                start = startPainter,
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
                preferExactIntrinsicSize = !CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE
            ),
        ).map {
            it.first to BackgroundPainter(
                it.second,
                Color.Green.copy(alpha = 0.5f)
            )
        }
    }
}