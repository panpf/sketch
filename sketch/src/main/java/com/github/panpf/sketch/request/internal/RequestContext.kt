package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.util.requiredMainThread

class RequestContext {

    private var pendingCountDrawable: SketchCountBitmapDrawable? = null

    @MainThread
    fun pendingCountDrawable(drawable: SketchCountBitmapDrawable, caller: String) {
        requiredMainThread()
        completeCountDrawable(caller)
        pendingCountDrawable = drawable.apply {
            countBitmap.setIsPending(true, caller)
        }
    }

    @MainThread
    fun completeCountDrawable(caller: String) {
        requiredMainThread()
        pendingCountDrawable?.countBitmap?.setIsPending(false, caller)
    }
}