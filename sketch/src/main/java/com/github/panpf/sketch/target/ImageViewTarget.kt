package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.request.internal.ViewTargetRequestDelegate
import com.github.panpf.sketch.util.foreachSketchCountDrawable

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
            val oldDrawable = view.drawable
            value?.foreachSketchCountDrawable {
                it.setIsDisplayed(true, "ImageView")
            }
            view.setImageDrawable(value)
            oldDrawable?.foreachSketchCountDrawable {
                it.setIsDisplayed(false, "ImageView")
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ImageViewTarget && view == other.view
    }

    override fun hashCode() = view.hashCode()
}
