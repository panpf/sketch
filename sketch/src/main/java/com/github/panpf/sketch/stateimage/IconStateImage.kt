package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.IconDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.SketchException

class IconStateImage : StateImage {

    private val icon: DrawableFetcher
    private val bgColor: ColorFetcher?

    constructor(@DrawableRes iconRes: Int, bgColor: ColorFetcher? = null) {
        this.icon = ResDrawable(iconRes)
        this.bgColor = bgColor
    }

    constructor(icon: Drawable, bgColor: ColorFetcher? = null) {
        this.icon = RealDrawable(icon)
        this.bgColor = bgColor
    }

    override fun getDrawable(
        sketch: Sketch, request: ImageRequest, throwable: SketchException?
    ): Drawable {
        val icon = icon.getDrawable(sketch.context)
        val bgColor = bgColor?.getColor(sketch.context.resources)
        return IconDrawable(icon, bgColor)
    }
}