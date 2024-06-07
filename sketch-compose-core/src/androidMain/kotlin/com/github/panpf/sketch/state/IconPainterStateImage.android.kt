package com.github.panpf.sketch.state

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.util.DrawableEqualizer
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.ResDrawable
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.asEquality
import com.github.panpf.sketch.util.toSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = icon.asPainterEqualizer(),
            background = background?.asPainterEqualizer(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = icon.asPainterEqualizer(),
            background = backgroundDrawable?.asPainterEqualizer(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = icon.asPainterEqualizer(),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    IconPainterStateImage(
        icon = icon.asPainterEqualizer(),
        background = background?.asPainterEqualizer(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconPainterStateImage(
            icon = icon.asPainterEqualizer(),
            background = backgroundDrawable?.asPainterEqualizer(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    IconPainterStateImage(
        icon = icon.asPainterEqualizer(),
        background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = icon.asPainterEqualizer(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, iconSize, iconTint) {
    IconPainterStateImage(
        icon = icon.asPainterEqualizer(),
        background = null,
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.asPainterEqualizer(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = backgroundDrawable?.asPainterEqualizer(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.asPainterEqualizer(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = backgroundDrawable?.asPainterEqualizer(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainterStateImage(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}