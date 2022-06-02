package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.util.SketchException

class TestLoadTarget : LoadTarget {

    var start: String? = null
    var successBitmap: Bitmap? = null
    var exception: SketchException? = null

    override fun onStart() {
        super.onStart()
        this.start = "onStart"
    }

    override fun onSuccess(result: Bitmap) {
        super.onSuccess(result)
        this.successBitmap = result
    }

    override fun onError(exception: SketchException) {
        super.onError(exception)
        this.exception = exception
    }
}