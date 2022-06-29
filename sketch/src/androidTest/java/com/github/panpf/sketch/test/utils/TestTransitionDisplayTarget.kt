package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.transition.TransitionDisplayTarget

class TestTransitionDisplayTarget : DisplayTarget, TransitionDisplayTarget {

    override var drawable: Drawable? = null

    override fun onStart(placeholder: Drawable?) {
        this.drawable = placeholder
    }

    override fun onSuccess(result: Drawable) {
        this.drawable = result
    }

    override fun onError(error: Drawable?) {
        this.drawable = error
    }
}