package com.github.panpf.sketch.state

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor

fun IntColorDrawableStateImage(@ColorInt color: Int): ColorDrawableStateImage =
    ColorDrawableStateImage(IntColor(color))

fun ResColorDrawableStateImage(@ColorRes colorRes: Int): ColorDrawableStateImage =
    ColorDrawableStateImage(ResColor(colorRes))

/**
 * Use color as the state [Drawable]
 */
class ColorDrawableStateImage(val color: ColorFetcher) : StateImage {

    override val key: String = "ColorDrawableStateImage(${color.key})"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image {
        return ColorDrawable(color.getColor(request.context)).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorDrawableStateImage) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "ColorDrawableStateImage($color)"
    }
}