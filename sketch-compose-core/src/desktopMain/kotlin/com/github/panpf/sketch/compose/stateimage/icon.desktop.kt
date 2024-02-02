package com.github.panpf.sketch.compose.stateimage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.compose.painter.rememberIconPainter


@Composable
fun rememberIconPainterStateImage(
    iconPath: String,
    backgroundPath: String? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val painter = rememberIconPainter(
        iconPath = iconPath,
        backgroundPath = backgroundPath,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(painter) { PainterStateImage(painter) }
}

@Composable
fun rememberIconPainterStateImage(
    iconPath: String,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val painter = rememberIconPainter(
        iconPath = iconPath,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(painter) { PainterStateImage(painter) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    iconPath: String,
    backgroundPath: String? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val painter = rememberIconAnimatablePainter(
        iconPath = iconPath,
        backgroundPath = backgroundPath,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(painter) { PainterStateImage(painter) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    iconPath: String,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val painter = rememberIconAnimatablePainter(
        iconPath = iconPath,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(painter) { PainterStateImage(painter) }
}