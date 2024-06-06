package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.internal.ImageGenerator


@Composable
fun rememberColorStateImage(color: Color): ColorStateImage =
    remember(color.value) { ColorStateImage(color) }

fun ColorStateImage(color: Color): ColorStateImage = ColorStateImage(ColorImageGenerator(color))

class ColorImageGenerator(val color: Color) : ImageGenerator {

    override val key: String = "IntColorImageGenerator(${color.value})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return ColorPainter(color).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorImageGenerator) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "IntColorImageGenerator(${color.value})"
    }
}