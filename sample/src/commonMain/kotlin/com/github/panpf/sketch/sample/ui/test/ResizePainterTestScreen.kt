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
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.numbers
import com.github.panpf.sketch.sample.ui.util.WrapperPainter
import com.github.panpf.sketch.sample.ui.util.toImageBitmap
import org.jetbrains.compose.resources.painterResource

class ResizePainterTestScreen : BasePainterTestScreen() {

    private var numbersPainter: Painter? = null
    private var density: Density? = null
    private var layoutDirection: LayoutDirection? = null

    @Composable
    override fun getTitle(): String = "ResizePainter"

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
        gridDividerSizePx: Float,
    ): List<Pair<String, Painter>> {
        val density = density!!
        val numbersPainter = numbersPainter!!
        val layoutDirection = layoutDirection!!
        val gridWidth = (containerSize.width - (cells + 1) * gridDividerSizePx) / cells
        val containerWidth = gridWidth * 0.75f
        val smallImageWidth = gridWidth * 0.5f
        val smallImageBitmap = numbersPainter.toImageBitmap(
            density = density,
            layoutDirection = layoutDirection,
            size = Size(smallImageWidth, smallImageWidth)
        )
        val bigImageWidth = gridWidth * 1.5f
        val bigImageBitmap = numbersPainter.toImageBitmap(
            density = density,
            layoutDirection = layoutDirection,
            size = Size(bigImageWidth, bigImageWidth)
        )
        val smallPainter = BitmapPainter(smallImageBitmap)
        val bigPainter = BitmapPainter(bigImageBitmap)
        return mutableListOf<Pair<String, Painter>>(
            "Small" to ResizePainter(
                painter = smallPainter,
                size = Size(containerWidth, containerWidth),
                contentScale = contentScale,
                alignment = alignment,
            ),
            "Big" to ResizePainter(
                painter = bigPainter,
                size = Size(containerWidth, containerWidth),
                contentScale = contentScale,
                alignment = alignment,
            ),
        ).map {
            it.first to WrapperPainter(
                it.second,
                Color.Green.copy(alpha = 0.5f)
            )
        }
    }
}