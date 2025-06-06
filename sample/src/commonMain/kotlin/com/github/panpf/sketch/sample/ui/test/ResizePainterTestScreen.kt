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
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.ui.util.BackgroundPainter
import com.github.panpf.sketch.sample.ui.util.scale

class ResizePainterTestScreen : BasePainterTestScreen() {

    @Composable
    override fun getTitle(): String = "ResizePainter"

    override suspend fun buildPainters(
        context: PlatformContext,
        contentScale: ContentScale,
        alignment: Alignment,
        itemWidth: Float,
    ): List<Pair<String, Painter>> {
        val containerSize = Size(
            width = itemWidth * 0.75f,
            height = itemWidth * 0.65f
        )
        val numbersBitmap = Res.readBytes("drawable/numbers.jpg").decodeToImageBitmap()
        val smallImageWidth = itemWidth * 0.5f
        val smallImageBitmap = numbersBitmap.scale(
            size = IntSize(
                width = smallImageWidth.fastRoundToInt(),
                height = smallImageWidth.fastRoundToInt()
            )
        )
        val smallPainter = BitmapPainter(smallImageBitmap)
        val bigImageWidth = itemWidth * 1.5f
        val bigImageBitmap = numbersBitmap.scale(
            size = IntSize(
                width = bigImageWidth.fastRoundToInt(),
                height = bigImageWidth.fastRoundToInt()
            )
        )
        val bigPainter = BitmapPainter(bigImageBitmap)
        return mutableListOf<Pair<String, Painter>>(
            "Small" to ResizePainter(
                painter = smallPainter,
                size = containerSize,
                contentScale = contentScale,
                alignment = alignment,
            ),
            "Big" to ResizePainter(
                painter = bigPainter,
                size = containerSize,
                contentScale = contentScale,
                alignment = alignment,
            ),
        ).map {
            it.first to BackgroundPainter(
                it.second,
                Color.Green.copy(alpha = 0.5f)
            )
        }
    }
}