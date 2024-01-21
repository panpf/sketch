package com.github.panpf.sketch.compose.stateimage

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.IconPainter

fun iconPainterStateImage(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage = PainterStateImage(
    IconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
)

fun iconPainterStateImage(
    icon: Painter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage = PainterStateImage(
    IconPainter(
        icon = icon,
        background = background?.let { ColorPainter(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
)