package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ProgressListener

class DisplayProgressListenerSupervisor(
    private val name: String? = null,
    private val onUpdateProgress: (() -> Unit)? = null
) : ProgressListener<DisplayRequest> {

    val callbackActionList = mutableListOf<String>()

    override fun onUpdateProgress(
        request: DisplayRequest,
        totalLength: Long,
        completedLength: Long
    ) {
        callbackActionList.add(completedLength.toString() + (name?.let { ":$it" } ?: ""))
        onUpdateProgress?.invoke()
    }
}