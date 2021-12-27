package com.github.panpf.sketch.common.internal

import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.Listener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ListenerDelegate<REQUEST : ImageRequest, RESULT : ImageResult>(
    private val listener: Listener<REQUEST, RESULT>
) {

    suspend fun onStart(request: REQUEST) {
        withContext(Dispatchers.Main) {
            listener.onStart(request)
        }
    }

    suspend fun onCancel(request: REQUEST) {
        // Because the current coroutine has been cancelled at this time, you must specify NonCancellable to execute successfully
        withContext(Dispatchers.Main + NonCancellable) {
            listener.onCancel(request)
        }
    }

    suspend fun onError(request: REQUEST, throwable: Throwable) {
        withContext(Dispatchers.Main) {
            listener.onError(request, throwable)
        }
    }

    suspend fun onSuccess(request: REQUEST, result: RESULT) {
        withContext(Dispatchers.Main) {
            listener.onSuccess(request, result)
        }
    }
}