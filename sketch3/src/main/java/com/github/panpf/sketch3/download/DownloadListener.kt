package com.github.panpf.sketch3.download

import androidx.annotation.MainThread

/**
 * A set of callbacks for an [DownloadRequest].
 */
interface DownloadListener {

    /**
     * Called if the request is started.
     */
    @MainThread
    fun onStart(request: DownloadRequest) {
    }

    /**
     * Called if the request is cancelled.
     */
    @MainThread
    fun onCancel(request: DownloadRequest) {
    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(request: DownloadRequest, throwable: Throwable) {
    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(request: DownloadRequest, result: DownloadData) {
    }
}