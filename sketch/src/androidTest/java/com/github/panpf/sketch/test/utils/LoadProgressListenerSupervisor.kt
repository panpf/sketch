package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.ProgressListener

class LoadProgressListenerSupervisor(
    private val name: String? = null,
    private val onUpdateProgress: (() -> Unit)? = null
) : ProgressListener<LoadRequest> {

    val callbackActionList = mutableListOf<String>()

    override fun onUpdateProgress(
        request: LoadRequest,
        totalLength: Long,
        completedLength: Long
    ) {
        callbackActionList.add(completedLength.toString() + (name?.let { ":$it" } ?: ""))
        onUpdateProgress?.invoke()
    }
}