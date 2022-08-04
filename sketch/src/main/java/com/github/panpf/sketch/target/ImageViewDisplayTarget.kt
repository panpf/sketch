package com.github.panpf.sketch.target

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.request.internal.ViewTargetRequestDelegate
import com.github.panpf.sketch.util.foreachSketchCountDrawable

/**
 * A [Target] that handles setting images on an [ImageView].
 */
open class ImageViewDisplayTarget(override val view: ImageView) :
    GenericViewDisplayTarget<ImageView>() {

    /**
     * @see [ViewTargetRequestDelegate.onViewDetachedFromWindow]
     */
    override var drawable: Drawable?
        get() = view.drawable
        set(value) {
            val oldDrawable = view.drawable
            value?.foreachSketchCountDrawable {
                it.countBitmap.setIsDisplayed(true, "ImageView")
            }
            if (value is CrossfadeDrawable) {
                value.start?.let { start ->
                    require(start.callback != null) { "start.callback is null. set before" }
                }
                require(value.end?.callback != null) { "end.callback is null. set before" }
            }
            view.setImageDrawable(value)
            if (value is CrossfadeDrawable) {
                value.start?.let { start ->
                    if (start === oldDrawable) {
                        require(start.callback == null) { "start.callback is not null. set after" }
                        start.callback = value
                    } else {
                        require(start.callback != null) { "start.callback is null. set after" }
                    }
                }
                require(value.end?.callback != null) { "end.callback is null. set after" }
            }
            oldDrawable?.foreachSketchCountDrawable {
                it.countBitmap.setIsDisplayed(false, "ImageView")
            }
            if (oldDrawable is Animatable) {
                oldDrawable.stop()
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ImageViewDisplayTarget && view == other.view
    }

    override fun hashCode() = view.hashCode()
}
