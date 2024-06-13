package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.PainterEqualizer
import com.github.panpf.sketch.painter.asEquality
import com.github.panpf.sketch.painter.rememberEqualityPainterResource
import com.github.panpf.sketch.request.ImageRequest
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

// TODO Looking forward to Compose Multiplatform supporting ColorResource


@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter?.asEquality(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
fun rememberIconPainterStateImage(
    icon: PainterEqualizer,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    return remember(icon, iconSize, iconTint) {
        IconPainterStateImage(
            icon = icon,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter?.asEquality(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

@Stable
class IconPainterStateImage(
    val icon: PainterEqualizer,
    val background: PainterEqualizer? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : StateImage {

    override val key: String =
        "IconPainterStateImage(icon=${icon.key},background=${background?.key},iconSize=$iconSize,iconTint=$iconTint)"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image? {
        return IconPainter(icon, background, iconSize, iconTint).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IconPainterStateImage
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        if (iconTint != other.iconTint) return false
        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconPainterStateImage(icon=$icon, background=$background, iconSize=$iconSize, iconTint=$iconTint)"
    }
}