package com.github.panpf.sketch.stateimage

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.IconDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.SketchException

class IconStateImage : StateImage {

    private val icon: DrawableFetcher
    private val bg: Any?

    constructor(@DrawableRes icon: Int, bg: ColorFetcher? = null) {
        this.icon = ResDrawable(icon)
        this.bg = bg
    }

    constructor(icon: Drawable, bg: ColorFetcher? = null) {
        this.icon = RealDrawable(icon)
        this.bg = bg
    }

    constructor(@DrawableRes icon: Int, bg: DrawableFetcher? = null) {
        this.icon = ResDrawable(icon)
        this.bg = bg
    }

    constructor(icon: Drawable, bg: DrawableFetcher? = null) {
        this.icon = RealDrawable(icon)
        this.bg = bg
    }

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Drawable {
        val icon = icon.getDrawable(request.context)
        val bgDrawable = when (bg) {
            is DrawableFetcher -> bg.getDrawable(request.context)
            is ColorFetcher -> ColorDrawable(bg.getColor(request.context))
            else -> null
        }
        return IconDrawable(icon, bgDrawable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconStateImage

        if (icon != other.icon) return false
        if (bg != other.bg) return false

        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (bg?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconStateImage(icon=$icon, bg=$bg)"
    }
}