package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.numbers
import com.github.panpf.sketch.sample.ui.util.SizeColorPainter
import com.github.panpf.sketch.sample.ui.util.WrapperPainter
import com.github.panpf.sketch.sample.ui.util.toImageBitmap
import com.github.panpf.sketch.transition.CrossfadeTransition
import org.jetbrains.compose.resources.painterResource

class CrossfadePainterTestScreen : BasePainterTestScreen() {

    private var numbersPainter: Painter? = null
    private var density: Density? = null
    private var layoutDirection: LayoutDirection? = null

    @Composable
    override fun getTitle(): String = "CrossfadePainter"

    @Composable
    override fun BuildInitial() {
        super.BuildInitial()
        numbersPainter = painterResource(Res.drawable.numbers)
        density = LocalDensity.current
        layoutDirection = LocalLayoutDirection.current
    }

    override suspend fun buildPainters(
        context: PlatformContext,
        contentScale: ContentScale,
        alignment: Alignment,
        containerSize: IntSize,
        cells: Int,
        gridDividerSizePx: Float
    ): List<Pair<String, Painter>> {
        val density = density!!
        val numbersPainter = numbersPainter!!
        val layoutDirection = layoutDirection!!
        val gridWidth = (containerSize.width - (cells + 1) * gridDividerSizePx) / cells
        val startPainterWidth = gridWidth * 0.75f
        val endPainterWidth = gridWidth * 0.5f
        val endImageBitmap = numbersPainter.toImageBitmap(
            density = density,
            layoutDirection = layoutDirection,
            size = Size(endPainterWidth, endPainterWidth)
        )
        val endPainter = BitmapPainter(endImageBitmap)
        return mutableListOf<Pair<String, Painter>>(
            "Default" to CrossfadePainter(
                start = SizeColorPainter(
                    Color.Blue,
                    Size(startPainterWidth, startPainterWidth)
                ),
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
            ),
            "Long Duration" to CrossfadePainter(
                start = SizeColorPainter(
                    Color.Blue,
                    Size(startPainterWidth, startPainterWidth)
                ),
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
                durationMillis = CrossfadeTransition.DEFAULT_DURATION_MILLIS * 4
            ),
            "No fadeStart" to CrossfadePainter(
                start = SizeColorPainter(
                    Color.Blue,
                    Size(startPainterWidth, startPainterWidth)
                ),
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
                fadeStart = !CrossfadeTransition.DEFAULT_FADE_START
            ),
            "PreferExactIntrinsicSize" to CrossfadePainter(
                start = SizeColorPainter(
                    Color.Blue,
                    Size(startPainterWidth, startPainterWidth)
                ),
                end = endPainter,
                contentScale = contentScale,
                alignment = alignment,
                preferExactIntrinsicSize = !CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE
            ),
        ).map {
            it.first to WrapperPainter(
                it.second,
                Color.Green.copy(alpha = 0.5f)
            )
        }
    }
}