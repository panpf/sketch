package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.IconStateImage
import com.github.panpf.sketch.state.IntColor


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