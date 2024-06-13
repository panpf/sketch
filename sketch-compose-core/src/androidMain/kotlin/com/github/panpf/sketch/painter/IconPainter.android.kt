package com.github.panpf.sketch.painter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.drawable.asEquality
import com.github.panpf.sketch.state.asPainterEqualizer
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.drawable.IntColor
import com.github.panpf.sketch.drawable.ResColor
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.toSize


/* ********************************************* drawable icon ********************************************* */

@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = icon.asPainterEqualizer(),
            background = background?.asPainterEqualizer(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
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
            icon = icon.asPainterEqualizer(),
            background = backgroundDrawable?.asPainterEqualizer(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
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
            icon = icon.asPainterEqualizer(),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter = remember(icon, background, iconSize, iconTint) {
    IconPainter(
        icon = icon.asPainterEqualizer(),
        background = background?.asPainterEqualizer(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
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
            icon = icon.asPainterEqualizer(),
            background = backgroundDrawable?.asPainterEqualizer(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter = remember(icon, background, iconSize, iconTint) {
    IconPainter(
        icon = icon.asPainterEqualizer(),
        background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconPainter(
            icon = icon.asPainterEqualizer(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

@Composable
fun rememberIconPainter(
    icon: DrawableEqualizer,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter = remember(icon, iconSize, iconTint) {
    IconPainter(
        icon = icon.asPainterEqualizer(),
        background = null,
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


/* ********************************************* res icon ********************************************* */

@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
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
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.asPainterEqualizer(),
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
            icon = iconDrawable.asPainterEqualizer(icon),
            background = backgroundDrawable?.asPainterEqualizer(background),
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
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


@Composable
fun rememberIconPainter(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconPainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconPainter(
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.asPainterEqualizer(),
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
            icon = iconDrawable.asPainterEqualizer(icon),
            background = backgroundDrawable?.asPainterEqualizer(background),
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
            icon = iconDrawable.asPainterEqualizer(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquality(),
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
            icon = iconDrawable.asPainterEqualizer(icon),
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
            icon = iconDrawable.asPainterEqualizer(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}