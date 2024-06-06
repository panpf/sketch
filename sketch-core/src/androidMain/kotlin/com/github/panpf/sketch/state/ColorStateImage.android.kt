package com.github.panpf.sketch.state

import android.graphics.drawable.ColorDrawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.internal.ImageGenerator


fun IntColorStateImage(@ColorInt color: Int): ColorStateImage =
    ColorStateImage(IntColorImageGenerator(color))

fun ResColorStateImage(@ColorRes resId: Int): ColorStateImage =
    ColorStateImage(ResColorImageGenerator(resId))

class IntColorImageGenerator(@ColorInt val color: Int) : ImageGenerator {

    override val key: String = "IntColorImageGenerator(${color})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return ColorDrawable(color).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntColorImageGenerator) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "IntColorImageGenerator($color)"
    }
}

class ResColorImageGenerator(@ColorRes val resId: Int) : ImageGenerator {

    override val key: String = "ResColorImageGenerator(${resId})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        val color = ResourcesCompat.getColor(request.context.resources, resId, null)
        return ColorDrawable(color).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntColorImageGenerator) return false
        if (resId != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return resId.hashCode()
    }

    override fun toString(): String {
        return "ResColorImageGenerator($resId)"
    }
}