package com.github.panpf.sketch.compose.painter

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.compose.internal.toSize
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.state.ResColor
import com.github.panpf.sketch.util.ResDrawable
import com.github.panpf.sketch.util.SketchSize

@Composable
fun rememberIconAnimatablePainter(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainter(
            icon = icon.asPainter(),
            background = background?.asPainter(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    IconAnimatablePainter(
        icon = icon.asPainter(),
        background = background?.asPainter(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainter(
            icon = iconDrawable.asPainter(),
            background = backgroundDrawable.asPainter(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconAnimatablePainter(
            icon = iconDrawable.asPainter(),
            background = backgroundDrawable.asPainter(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainter(
            icon = iconDrawable.asPainter(),
            background = background?.let { ColorPainter(Color(it.color)) },
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconAnimatablePainter(
            icon = iconDrawable.asPainter(),
            background = background?.let { ColorPainter(Color(it.color)) },
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainter(
            icon = iconDrawable.asPainter(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconAnimatablePainter(
            icon = iconDrawable.asPainter(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}