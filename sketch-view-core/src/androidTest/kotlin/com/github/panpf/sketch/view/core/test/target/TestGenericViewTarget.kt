package com.github.panpf.sketch.view.core.test.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.target.GenericViewTarget

class TestGenericViewTarget(override val view: ImageView) : GenericViewTarget<ImageView>(view) {

    override fun setDrawable(drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }

    override val drawable: Drawable?
        get() = view.drawable

    override val scaleType: ScaleType
        get() = view.scaleType ?: ScaleType.FIT_CENTER

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