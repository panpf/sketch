package com.github.panpf.sketch.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.SketchPainter
import com.github.panpf.sketch.painter.internal.toLogString
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@ExperimentalResourceApi
@Composable
fun equalityPainterResource(resource: DrawableResource): PainterEqualizer {
    val painter = painterResource(resource)
    return PainterEqualizer(wrapped = painter, equalityKey = resource)
}

fun Painter.asEquality(equalKey: Any): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = equalKey)

fun ColorPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = this)

fun BrushPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = this)

fun BitmapPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = this)

fun SketchPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this as Painter, equalityKey = this)

/**
 * The VectorPainter equals returned by two consecutive calls to painterResource() on the same vector drawable resource is false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 */
@Stable
class PainterEqualizer(
    override val wrapped: Painter,
    override val equalityKey: Any
) : Equalizer<Painter> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PainterEqualizer) return false
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "PainterEqualizer(wrapped=${wrapped.toLogString()}, equalityKey=$equalityKey)"
    }
}