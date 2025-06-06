package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.PlatformContext

expect suspend fun buildPainterContentScaleTestPainters(
    context: PlatformContext,
    contentScale: ContentScale,
    alignment: Alignment,
): List<Pair<String, Painter>>

class MixPainterTestScreen : BasePainterTestScreen() {

    @Composable
    override fun getTitle(): String = "Mix Painter"

    override suspend fun buildPainters(
        context: PlatformContext,
        contentScale: ContentScale,
        alignment: Alignment,
        containerSize: IntSize,
        cells: Int,
        gridDividerSizePx: Float,
    ): List<Pair<String, Painter>> =
        buildPainterContentScaleTestPainters(context, contentScale, alignment)
}