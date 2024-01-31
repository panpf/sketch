package com.github.panpf.sketch.compose.request.internal

import com.github.panpf.sketch.compose.AsyncImageState
import com.github.panpf.sketch.request.internal.BaseRequestManager

class ComposeTargetRequestManager(
    private val asyncImageState: AsyncImageState
) : BaseRequestManager() {

    fun onRemembered() {
        // AsyncImageState will always perform a request when it is remembered,
        // so there is nothing to do here
    }

    fun onForgotten() {
        currentRequestDelegate?.dispose()
        callbackAttachedState()
    }

    override fun isAttached(): Boolean {
        return asyncImageState.isRemembered()
    }
}