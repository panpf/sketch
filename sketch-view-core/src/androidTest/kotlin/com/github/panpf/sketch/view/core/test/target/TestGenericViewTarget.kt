package com.github.panpf.sketch.view.core.test.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.target.GenericViewTarget
import com.github.panpf.sketch.util.fitScale

class TestGenericViewTarget(override val view: ImageView) : GenericViewTarget<ImageView>(view) {

    override var drawable: Drawable?
        get() = view.drawable
        set(value) {
            view.setImageDrawable(value)
        }

    override val fitScale: Boolean
        get() = view.scaleType.fitScale

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestGenericViewTarget
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