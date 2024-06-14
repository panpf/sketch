package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.PainterEqualizer
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.painter.rememberEqualityPainterResource
import org.jetbrains.compose.resources.DrawableResource


@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter?.asEquality(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}