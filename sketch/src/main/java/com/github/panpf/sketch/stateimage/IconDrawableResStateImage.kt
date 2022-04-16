package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.stateimage.internal.IconStateDrawable
import com.github.panpf.sketch.util.SketchException

// todo 合二为一，方案为 增加多个参数，比如 colorInt: Int, colorRes: Int， drawable: Drawable, drawableRes: Int
class IconDrawableResStateImage(
    @DrawableRes private val iconResId: Int,
    @ColorInt private val backgroundColor: Int? = null
) : StateImage {

    override fun getDrawable(
        sketch: Sketch, request: ImageRequest, throwable: SketchException?
    ): Drawable {
        val iconDrawable = AppCompatResources.getDrawable(sketch.context, iconResId)!!
        return IconStateDrawable(iconDrawable, backgroundColor)
    }
}