package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.target.DisplayTarget

class TestDisplayTarget : DisplayTarget {

    var startDrawable: Drawable? = null
    var successDrawable: Drawable? = null
    var errorDrawable: Drawable? = null

    override fun onStart(placeholder: Drawable?) {
        super.onStart(placeholder)
        startDrawable = placeholder
    }

    override fun onSuccess(result: Drawable) {
        super.onSuccess(result)
        successDrawable = result
    }

    override fun onError(error: Drawable?) {
        super.onError(error)
        errorDrawable = error
    }
}