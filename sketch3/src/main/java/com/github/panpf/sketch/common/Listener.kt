package com.github.panpf.sketch.common

import androidx.annotation.MainThread

/**
 * A set of callbacks for an [ImageRequest].
 */
interface Listener<REQUEST : ImageRequest, RESULT : ImageResult> {

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
    fun onError(request: REQUEST, throwable: Throwable) {
    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(request: REQUEST, result: RESULT) {
    }
}