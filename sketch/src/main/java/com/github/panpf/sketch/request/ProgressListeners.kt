package com.github.panpf.sketch.request

class ProgressListeners<REQUEST : ImageRequest>(
    val progressListenerList: List<ProgressListener<REQUEST>>,
) : ProgressListener<REQUEST> {

    constructor(vararg listeners: ProgressListener<REQUEST>) : this(listeners.toList())

    override fun onUpdateProgress(request: REQUEST, totalLength: Long, completedLength: Long) {
        progressListenerList.forEach {
            it.onUpdateProgress(request, totalLength, completedLength)
        }
    }
}