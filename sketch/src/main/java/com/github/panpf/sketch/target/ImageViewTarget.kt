package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.util.findCountDrawable

/**
 * A [Target] that handles setting images on an [ImageView].
 */
open class ImageViewTarget(override val view: ImageView) : GenericViewTarget<ImageView>() {

    override var drawable: Drawable?
        get() = view.drawable
        set(value) {
            value?.findCountDrawable()?.setIsDisplayed("ImageViewTarget:set", true)
            view.drawable?.findCountDrawable()?.setIsDisplayed("ImageViewTarget:set", false)
            view.setImageDrawable(value)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ImageViewTarget && view == other.view
    }

    override fun hashCode() = view.hashCode()
}
