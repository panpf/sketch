package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest

@Composable
fun rememberColorPainterStateImage(color: Long): ColorPainterStateImage =
    remember(color) { ColorPainterStateImage(color) }

@Composable
fun rememberColorPainterStateImage(color: Int): ColorPainterStateImage =
    remember(color) { ColorPainterStateImage(color) }

@Composable
fun rememberColorPainterStateImage(color: Color): ColorPainterStateImage =
    remember(color) { ColorPainterStateImage(color) }

fun ColorPainterStateImage(color: Int): ColorPainterStateImage =
    ColorPainterStateImage(Color(color))

fun ColorPainterStateImage(color: Long): ColorPainterStateImage =
    ColorPainterStateImage(Color(color))

@Stable
class ColorPainterStateImage(val color: Color) : StateImage {

    override val key: String = "ColorPainterStateImage(${color.value})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return ColorPainter(color).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorPainterStateImage) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "ColorPainterStateImage(${color.value})"
    }
}