package com.github.panpf.sketch.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.rememberIconPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

// TODO Looking forward to Compose Multiplatform supporting ColorResource


@Composable
fun rememberIconPainterStateImage(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: Painter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: Painter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: Painter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}


@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}