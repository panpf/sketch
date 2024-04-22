package com.github.panpf.sketch.state

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.util.DrawableEqualizer
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.util.asEquality
import com.github.panpf.sketch.util.SketchSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
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
    icon: DrawableEqualizer,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}


@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
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
    icon: DrawableEqualizer,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}


@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}


@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
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
    return remember(iconPainter) { PainterStateImage(iconPainter.asEquality()) }
}