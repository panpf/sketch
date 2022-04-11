package com.github.panpf.sketch.request

import androidx.annotation.MainThread

/**
 * A set of callbacks for an [ImageRequest].
 */
interface Listener<REQUEST : ImageRequest, SUCCESS : ImageResult.Success, ERROR : ImageResult.Error> {

    /**
     * Called if the request is started.
     */
    @MainThread
    fun onStart(request: REQUEST) {
    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(request: REQUEST, result: SUCCESS) {
    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(request: REQUEST, result: ERROR) {
    }

    /**
     * Called if the request is cancelled.
     */
    @MainThread
    fun onCancel(request: REQUEST) {
    }
}