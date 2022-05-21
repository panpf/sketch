package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.util.requiredMainThread

class RequestContext {

    private var pendingCountDrawable: SketchCountBitmapDrawable? = null

    @MainThread
    fun pendingCountDrawable(drawable: SketchCountBitmapDrawable, callingStation: String) {
        requiredMainThread()
        completeCountDrawable(callingStation)
        pendingCountDrawable = drawable.apply {
            setIsPending(callingStation, true)
        }
    }

    @MainThread
    fun completeCountDrawable(callingStation: String) {
        requiredMainThread()
        pendingCountDrawable?.setIsPending(callingStation, false)
    }
}