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
        sketch: Sketch, request: ImageRequest, exception: SketchException?
    ): Drawable {
        val icon = icon.getDrawable(sketch.context)
        val bgDrawable = when (bg) {
            is DrawableFetcher -> bg.getDrawable(sketch.context)
            is ColorFetcher -> ColorDrawable(bg.getColor(sketch.context))
            else -> null
        }
        return IconDrawable(icon, bgDrawable)
    }
}