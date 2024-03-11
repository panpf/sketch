package com.github.panpf.sketch.compose.painter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.res.ResourcesCompat

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
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
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainter {
    val iconPainter = painterResource(icon)
    val backgroundPainter = background?.let { painterResource(it) }
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResourcesCompat.getColor(context.resources, it, null) }
            ?.let { Color(it) }
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
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
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainter {
    val iconPainter = painterResource(icon)
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        val iconTintColor = iconTint
            ?.let { ResourcesCompat.getColor(context.resources, it, null) }
            ?.let { Color(it) }
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
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
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val iconPainter = painterResource(icon)
    val backgroundPainter = background?.let { painterResource(it) }
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResourcesCompat.getColor(context.resources, it, null) }
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
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

@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val iconPainter = painterResource(icon)
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        val iconTintColor = iconTint
            ?.let { ResourcesCompat.getColor(context.resources, it, null) }
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}