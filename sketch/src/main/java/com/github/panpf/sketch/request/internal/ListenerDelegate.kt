package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Listener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ListenerDelegate<REQUEST : ImageRequest, SUCCESS_RESULT : ImageResult, ERROR_RESULT: ImageResult>(
    private val listener: Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>
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

    suspend fun onError(request: REQUEST, result: ERROR_RESULT) {
        withContext(Dispatchers.Main) {
            listener.onError(request, result)
        }
    }

    suspend fun onSuccess(request: REQUEST, result: SUCCESS_RESULT) {
        withContext(Dispatchers.Main) {
            listener.onSuccess(request, result)
        }
    }
}