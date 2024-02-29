package com.github.panpf.sketch.compose.stateimage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.compose.painter.rememberAnimatableIconPainter
import com.github.panpf.sketch.compose.painter.rememberIconPainter


@Composable
fun rememberIconPainterStateImage(
    iconPath: String,
    backgroundPath: String? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        iconPath = iconPath,
        backgroundPath = backgroundPath,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberIconPainterStateImage(
    iconPath: String,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        iconPath = iconPath,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberAnimatableIconPainterStateImage(
    iconPath: String,
    backgroundPath: String? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val animatableIconPainter = rememberAnimatableIconPainter(
        iconPath = iconPath,
        backgroundPath = backgroundPath,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(animatableIconPainter) { PainterStateImage(animatableIconPainter) }
}

@Composable
fun rememberAnimatableIconPainterStateImage(
    iconPath: String,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val animatableIconPainter = rememberAnimatableIconPainter(
        iconPath = iconPath,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(animatableIconPainter) { PainterStateImage(animatableIconPainter) }
}