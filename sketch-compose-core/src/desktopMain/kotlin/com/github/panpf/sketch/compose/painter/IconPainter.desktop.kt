package com.github.panpf.sketch.compose.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource


@Composable
fun rememberIconPainter(
    icon: String,
    background: String? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val iconPainter = painterResource(icon)
    val backgroundPainter = background?.let { painterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: String,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val iconPainter = painterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: String,
    background: String? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = painterResource(icon)
    val backgroundPainter = background?.let { painterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: String,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = painterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}