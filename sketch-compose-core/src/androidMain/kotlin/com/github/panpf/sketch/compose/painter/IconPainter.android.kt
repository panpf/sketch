package com.github.panpf.sketch.compose.painter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.compose.internal.toSize
import com.github.panpf.sketch.compose.state.asEqualWrapper
import com.github.panpf.sketch.compose.state.asPainterEqualWrapper
import com.github.panpf.sketch.state.DrawableEqualWrapper
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.state.ResColor
import com.github.panpf.sketch.util.ResDrawable
import com.github.panpf.sketch.util.SketchSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = background?.asPainterEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = backgroundDrawable?.asPainterEqualWrapper(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter = remember(icon, background, iconSize, iconTint) {
    IconPainter(
        icon = icon.asPainterEqualWrapper(),
        background = background?.asPainterEqualWrapper(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = backgroundDrawable?.asPainterEqualWrapper(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    return remember(icon, background, iconSize, iconTint) {
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualWrapper,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    return remember(icon, iconSize, iconTint) {
        IconPainter(
            icon = icon.asPainterEqualWrapper(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = background?.asPainterEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = backgroundDrawable?.asPainterEqualWrapper(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: DrawableEqualWrapper? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = background?.asPainterEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = backgroundDrawable?.asPainterEqualWrapper(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEqualWrapper(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainter(
            icon = iconDrawable.asPainterEqualWrapper(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}