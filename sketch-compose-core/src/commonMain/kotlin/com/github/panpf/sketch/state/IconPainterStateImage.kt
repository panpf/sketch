package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.util.PainterEqualizer
import com.github.panpf.sketch.util.asEquality
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

// TODO Looking forward to Compose Multiplatform supporting ColorResource


@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}


@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}