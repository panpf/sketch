package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ProgressListenerDelegate<REQUEST : ImageRequest>(
    private val coroutineScope: CoroutineScope,
    private val progressListener: ProgressListener<REQUEST>
) {

    private var lastDeferred: Deferred<*>? = null

    @Suppress("DeferredResultUnused")
    fun onUpdateProgress(request: REQUEST, totalLength: Long, completedLength: Long) {
        val lastDeferred = this.lastDeferred
        if (lastDeferred?.isActive == true) {
            lastDeferred.cancel()
        }
        this.lastDeferred = coroutineScope.async(Dispatchers.Main) {
            progressListener.onUpdateProgress(request, totalLength, completedLength)
        }
    }
}