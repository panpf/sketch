package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext

@Composable
fun PainterMixTestScreen() {
    BasePainterTestScreen(
        title = "PainterMixTest",
        buildPainters = { context, contentScale, alignment, _ ->
            buildPainterContentScaleTestPainters(context, contentScale, alignment)
        }
    )
}

expect suspend fun buildPainterContentScaleTestPainters(
    context: PlatformContext,
    contentScale: ContentScale,
    alignment: Alignment,
): List<Pair<String, Painter>>