package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ProgressListenerDelegate<REQUEST : ImageRequest>(val progressListener: ProgressListener<REQUEST>) {
    suspend fun onUpdateProgress(request: REQUEST, totalLength: Long, completedLength: Long) {
        coroutineScope {
            async(Dispatchers.Main) {
                progressListener.onUpdateProgress(request, totalLength, completedLength)
            }
        }
    }
}