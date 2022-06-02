package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ProgressListener

class DownloadProgressListenerSupervisor(private val onUpdateProgress: (() -> Unit)? = null) :
    ProgressListener<DownloadRequest> {

    val callbackActionList = mutableListOf<String>()

    override fun onUpdateProgress(
        request: DownloadRequest,
        totalLength: Long,
        completedLength: Long
    ) {
        callbackActionList.add(completedLength.toString())
        onUpdateProgress?.invoke()
    }
}