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
fun rememberIconAnimatablePainterStateImage(
    icon: PainterEqualizer,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter?.asEquality(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}