package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.request.internal.BaseRequestManager

class ComposeTargetRequestManager(
    private val asyncImageState: AsyncImageState
) : BaseRequestManager() {

    fun onRemembered() {
        // AsyncImageState will always perform a request when it is remembered,
        // So there is no need to restart the request here
    }

    fun onForgotten() {
        currentRequestDelegate?.dispose()
        callbackAttachedState()
    }

    override fun isAttached(): Boolean {
        return asyncImageState.isRemembered()
    }
}