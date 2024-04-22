package com.github.panpf.sketch.state

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.util.DrawableEqualizer
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.util.asEquality
import com.github.panpf.sketch.util.SketchSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}


@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}


@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}


@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}


@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter.asEquality()) }
}