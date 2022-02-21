package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.stateimage.internal.IconStateDrawable
import com.github.panpf.sketch.util.SketchException

class IconStateImage(
    private val iconDrawable: Drawable,
    @ColorInt private val backgroundColor: Int? = null
) : StateImage {
    override fun getDrawable(
        sketch: Sketch, request: DisplayRequest, throwable: SketchException?
    ): Drawable {
        return IconStateDrawable(iconDrawable.mutate(), backgroundColor)
    }
}