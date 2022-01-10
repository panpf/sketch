package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ProgressListener

class CombinedProgressListener<REQUEST : ImageRequest>(
    val progressListeners: List<ProgressListener<REQUEST>>
) : ProgressListener<REQUEST> {

    override fun onUpdateProgress(request: REQUEST, totalLength: Long, completedLength: Long) {
        progressListeners.forEach {
            it.onUpdateProgress(request, totalLength, completedLength)
        }
    }
}