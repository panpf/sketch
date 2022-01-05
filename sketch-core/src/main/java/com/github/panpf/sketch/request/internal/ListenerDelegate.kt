package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Listener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ListenerDelegate<REQUEST : ImageRequest, DATA : ImageData>(
    private val listener: Listener<REQUEST, DATA>
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

    suspend fun onSuccess(request: REQUEST, data: DATA) {
        withContext(Dispatchers.Main) {
            listener.onSuccess(request, data)
        }
    }
}