package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

// TODO Looking forward to Compose Multiplatform supporting ColorResource


@Composable
fun rememberIconAnimatablePainter(
    icon: PainterEqualizer,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    IconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

@Composable
fun rememberIconAnimatablePainter(
    icon: PainterEqualizer,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.let { ColorPainter(it) }
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter?.asEquality(),
        iconSize = iconSize,
        iconTint = iconTint
    )
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainter(
    icon: PainterEqualizer,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val backgroundPainter = background?.let { equalityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: PainterEqualizer,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, iconSize, iconTint) {
    IconAnimatablePainter(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = equalityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = equalityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter?.asEquality(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = equalityPainterResource(icon)
    val backgroundPainter = background?.let { equalityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = equalityPainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


@Stable
class IconAnimatablePainter(
    icon: PainterEqualizer,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
) : IconPainter(icon, background, iconSize, iconTint), AnimatablePainter {

    private val animatablePainterIcon: AnimatablePainter?
    private val animatablePainterBackground: AnimatablePainter?

    override val key: String =
        "IconAnimatablePainter(icon=${icon.key},background=${background?.key},iconSize=$iconSize,iconTint=${iconTint?.value})"

    init {
        require(icon.wrapped is AnimatablePainter || background?.wrapped is AnimatablePainter) {
            "painter must be AnimatablePainter"
        }
        animatablePainterIcon = icon.wrapped as? AnimatablePainter
        animatablePainterBackground = background?.wrapped as? AnimatablePainter
    }

    override fun start() {
        animatablePainterIcon?.start()
        animatablePainterBackground?.start()
    }

    override fun stop() {
        animatablePainterIcon?.stop()
        animatablePainterBackground?.stop()
    }

    override fun isRunning(): Boolean {
        return animatablePainterIcon?.isRunning() == true || animatablePainterBackground?.isRunning() == true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconAnimatablePainter) return false
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        return iconTint == other.iconTint
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconAnimatablePainter(icon=${icon}, background=${background}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}