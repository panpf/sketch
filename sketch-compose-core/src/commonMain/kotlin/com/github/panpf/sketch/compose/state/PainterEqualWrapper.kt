package com.github.panpf.sketch.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.SketchPainter
import com.github.panpf.sketch.compose.painter.internal.toLogString
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@ExperimentalResourceApi
@Composable
fun equalWrapperPainterResource(resource: DrawableResource): PainterEqualWrapper {
    val painter = painterResource(resource)
    return PainterEqualWrapper(painter = painter, equalKey = resource)
}

fun Painter.asEqualWrapper(equalKey: Any): PainterEqualWrapper =
    PainterEqualWrapper(painter = this, equalKey = equalKey)

fun ColorPainter.asEqualWrapper(): PainterEqualWrapper =
    PainterEqualWrapper(painter = this, equalKey = this)

fun BrushPainter.asEqualWrapper(): PainterEqualWrapper =
    PainterEqualWrapper(painter = this, equalKey = this)

fun BitmapPainter.asEqualWrapper(): PainterEqualWrapper =
    PainterEqualWrapper(painter = this, equalKey = this)

fun SketchPainter.asEqualWrapper(): PainterEqualWrapper =
    PainterEqualWrapper(painter = this as Painter, equalKey = this)

/**
 * The VectorPainter equals returned by two consecutive calls to painterResource() on the same vector drawable resource is false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 */
class PainterEqualWrapper(val painter: Painter, val equalKey: Any) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PainterEqualWrapper) return false
        if (equalKey != other.equalKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalKey.hashCode()
    }

    override fun toString(): String {
        return "PainterEqualWrapper(painter=${painter.toLogString()}, equalKey=$equalKey)"
    }
}