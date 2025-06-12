package com.github.panpf.sketch.view.core.test.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewTarget

class TestViewTarget(
    override val view: ImageView? = null,
    override val scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
) : ViewTarget<ImageView> {

    override var drawable: Drawable?
        get() = view?.drawable
        set(value) {
            view?.setImageDrawable(value)
        }

    override fun getRequestManager(): RequestManager? {
        return view?.requestManager
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestViewTarget
        if (view != other.view) return false
        return true
    }

    override fun hashCode(): Int {
        return view.hashCode()
    }

    override fun toString(): String {
        return "TestViewTarget(view=$view)"
    }
}