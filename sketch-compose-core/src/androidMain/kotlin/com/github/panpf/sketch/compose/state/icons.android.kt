package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.compose.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.compose.painter.rememberIconPainter
import com.github.panpf.sketch.state.IconAnimatableStateImage
import com.github.panpf.sketch.state.IconStateImage
import com.github.panpf.sketch.state.IntColor


@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

// TODO Unable to match rememberIconPainterStateImage(icon = ..., background = ...)

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
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
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): PainterStateImage {
    val iconPainter = rememberIconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconPainter) { PainterStateImage(iconPainter) }
}

@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
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
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
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
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): PainterStateImage {
    val iconAnimatablePainter = rememberIconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
    return remember(iconAnimatablePainter) { PainterStateImage(iconAnimatablePainter) }
}


/**
 * Create and remember an IconStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    @ColorRes iconTint: Int,
): IconStateImage = remember(icon, background, iconSize, iconTint) {
    IconStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    iconTint: IntColor? = null,
): IconStateImage = remember(icon, background, iconSize, iconTint) {
    IconStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    @ColorRes iconTint: Int,
): IconStateImage = remember(icon, background, iconSize, iconTint) {
    IconStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    iconTint: IntColor? = null,
): IconStateImage = remember(icon, background, iconSize, iconTint) {
    IconStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    @ColorRes iconTint: Int,
): IconStateImage = remember(icon, background, iconSize, iconTint) {
    IconStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    iconTint: IntColor? = null,
): IconStateImage = remember(icon, background, iconSize, iconTint) {
    IconStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}


/**
 * Create and remember an IconAnimatableStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconAnimatableStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconAnimatableStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconAnimatableStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconAnimatableStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

/**
 * Create and remember an IconAnimatableStateImage. Set the size and background of the icon through trailing functions.
 */
@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: com.github.panpf.sketch.util.Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}