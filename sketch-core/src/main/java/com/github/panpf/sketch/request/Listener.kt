package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.internal.ImageData
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult

/**
 * A set of callbacks for an [ImageRequest].
 */
interface Listener<REQUEST : ImageRequest, SUCCESS_RESULT : ImageResult, ERROR_RESULT: ImageResult> {

    /**
     * Called if the request is started.
     */
    @MainThread
    fun onStart(request: REQUEST) {
    }

    /**
     * Called if the request is cancelled.
     */
    @MainThread
    fun onCancel(request: REQUEST) {
    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(request: REQUEST, result: ERROR_RESULT) {
    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(request: REQUEST, result: SUCCESS_RESULT) {
    }
}