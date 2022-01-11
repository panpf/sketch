package com.github.panpf.sketch.internal

import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.RequestDepth.LOCAL
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.request.isLocalDepthFromSaveCellularTraffic
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.sketch

class IgnoreSaveCellularTrafficHelper {

    var view: SketchImageView? = null

    val key: String by lazy {
        "IgnoreSaveCellularTrafficHelper"
    }

    val canIntercept: Boolean
        get() = view != null && errorFromSaveCellularTraffic && request != null

    private var errorFromSaveCellularTraffic = false
    private var request: DisplayRequest? = null

    fun onIntercept(): Boolean {
        if (!canIntercept) return false
        val view = view ?: return false
        val request = request ?: return false
        val newRequest = request.newDisplayRequest {
            ignoreSaveCellularTraffic(true)
            target(view)
        }
        view.context.sketch.enqueueDisplay(newRequest)
        return true
    }

    fun onRequestStart(request: DisplayRequest) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }

    fun onRequestError(request: DisplayRequest, result: Error) {
        val error = result.throwable
        errorFromSaveCellularTraffic = error is RequestDepthException && error.depth == LOCAL
                && error.thenRequest.isLocalDepthFromSaveCellularTraffic
        if (errorFromSaveCellularTraffic) {
            this.request = request
        } else {
            this.request = null
        }
    }

    fun onRequestSuccess(request: DisplayRequest, result: Success) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }
}