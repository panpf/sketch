package com.github.panpf.sketch.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.rememberIconAnimatablePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

// TODO Looking forward to Compose Multiplatform supporting ColorResource


@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: Painter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainterStateImage(
    icon: Painter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: Painter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}


@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}