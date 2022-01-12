package com.github.panpf.sketch.internal

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

class MimeTypeLogo {

    private val data: Any
    private var _drawable: Drawable? = null

    val hiddenWhenAnimatable: Boolean

    constructor(drawable: Drawable, hiddenWhenAnimatable: Boolean = false) {
        this.data = drawable
        this.hiddenWhenAnimatable = hiddenWhenAnimatable
    }

    constructor(drawableResId: Int, hiddenWhenAnimatable: Boolean = false) {
        this.data = drawableResId
        this.hiddenWhenAnimatable = hiddenWhenAnimatable
    }

    fun getDrawable(context: Context): Drawable {
        return _drawable ?: if (data is Drawable) {
            _drawable = data
            data
        } else {
            val drawableResId = data as Int
            val newDrawable = ResourcesCompat.getDrawable(context.resources, drawableResId, null)!!
            _drawable = newDrawable
            newDrawable
        }
    }
}