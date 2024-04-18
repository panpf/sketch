package com.github.panpf.sketch.compose.state

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.compose.painter.rememberIconPainter
import com.github.panpf.sketch.state.DrawableEqualWrapper
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.SketchSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}


@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}


@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualWrapper,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}


@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}


@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEqualWrapper()) }
}