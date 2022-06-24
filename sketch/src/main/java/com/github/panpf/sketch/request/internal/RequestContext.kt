package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.requiredMainThread

class RequestContext constructor(val firstRequest: ImageRequest) {

    private val _requests = mutableListOf(firstRequest)
    private var _lastRequest: ImageRequest = firstRequest
    private var pendingCountDrawable: SketchCountBitmapDrawable? = null

    val requests: List<ImageRequest>
        get() = _requests.toList()

    val lastRequest: ImageRequest
        get() = _lastRequest

    fun addRequest(request: ImageRequest) {
        val lastRequest = lastRequest
        if (lastRequest != request) {
            _requests.add(request)
            _lastRequest = request
        }
    }

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
        pendingCountDrawable?.apply {
            countBitmap.setIsPending(false, caller)
        }
    }
}