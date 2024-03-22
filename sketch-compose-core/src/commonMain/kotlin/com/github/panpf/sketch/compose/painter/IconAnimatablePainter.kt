package com.github.panpf.sketch.compose.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.internal.toLogString

@Composable
fun rememberIconAnimatablePainter(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: Painter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

class IconAnimatablePainter(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
) : IconPainter(icon, background, iconSize, iconTint), Animatable {

    private val animatableIcon: Animatable?
    private val animatableBackground: Animatable?

    init {
        require(icon is Animatable || background is Animatable) {
            "painter must be AnimatablePainter"
        }
        animatableIcon = icon as? Animatable
        animatableBackground = background as? Animatable
    }

    override fun start() {
        animatableIcon?.start()
        animatableBackground?.start()
    }

    override fun stop() {
        animatableIcon?.stop()
        animatableBackground?.stop()
    }

    override fun isRunning(): Boolean {
        return animatableIcon?.isRunning() == true || animatableBackground?.isRunning() == true
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
        return "IconAnimatablePainter(icon=${icon.toLogString()}, background=${background?.toLogString()}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}