package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.request.internal.ViewTargetRequestDelegate
import com.github.panpf.sketch.util.findLastCountDrawable

/**
 * A [Target] that handles setting images on an [ImageView].
 */
open class ImageViewTarget(override val view: ImageView) : GenericViewTarget<ImageView>() {

    /**
     * @see [ViewTargetRequestDelegate.onViewDetachedFromWindow]
     */
    override var drawable: Drawable?
        get() = view.drawable
        set(value) {
            value?.findLastCountDrawable()?.setIsDisplayed("ImageViewTarget:set", true)
            view.drawable?.findLastCountDrawable()?.setIsDisplayed("ImageViewTarget:set", false)
            view.setImageDrawable(value)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ImageViewTarget && view == other.view
    }

    override fun hashCode() = view.hashCode()
}
