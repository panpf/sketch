package com.github.panpf.sketch.stateimage

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.IconDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.SketchException

class IconStateImage private constructor(
    private val icon: DrawableFetcher,
    private val bg: Any?,
) : StateImage {

    constructor(icon: Drawable, bg: Drawable? = null)
            : this(RealDrawable(icon), bg?.let { RealDrawable(it) })

    constructor(icon: Drawable, @DrawableRes bg: Int? = null)
            : this(RealDrawable(icon), bg?.let { ResDrawable(it) })

    constructor(icon: Drawable, bg: ColorFetcher? = null)
            : this(RealDrawable(icon), bg)

    constructor(icon: Drawable)
            : this(RealDrawable(icon), null)

    constructor(@DrawableRes icon: Int, bg: Drawable? = null)
            : this(ResDrawable(icon), bg?.let { RealDrawable(it) })

    constructor(@DrawableRes icon: Int, @DrawableRes bg: Int? = null)
            : this(ResDrawable(icon), bg?.let { ResDrawable(it) })

    constructor(@DrawableRes icon: Int, bg: ColorFetcher? = null)
            : this(ResDrawable(icon), bg)

    constructor(@DrawableRes icon: Int)
            : this(ResDrawable(icon), null)

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
        if (other !is IconStateImage) return false

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