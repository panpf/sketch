package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.IconAnimatableStateImage
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.SketchSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}


@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}


@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    icon: Drawable,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}


@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}


@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}

@Composable
fun rememberIconAnimatableStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableStateImage(
        icon = icon,
        iconSize = iconSize,
        iconTint = iconTint,
    )
}