package com.github.panpf.sketch.target

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.util.SketchException

/**
 * Target dedicated to [DownloadRequest], which requires [DownloadData]
 */
interface DownloadTarget : Target {

    /**
     * Called when the request starts.
     */
    @MainThread
    fun onStart() {
    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(result: DownloadData) {
    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(exception: SketchException) {
    }
}