package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.compose.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.SketchSize


@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
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
    icon: Drawable,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
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
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
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
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
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
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
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
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
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
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
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
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}