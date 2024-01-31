package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestDelegate
import kotlinx.coroutines.Deferred

/**
 * Responsible for storing current requests and calling back events such as attach, detach.
 */
interface RequestManager {

    /** Attach [requestDelegate] to this view and cancel the old request. */
    @MainThread
    fun setRequest(requestDelegate: RequestDelegate?)

    /** Return 'true' if [disposable] is not attached to this view. */
    fun isDisposed(disposable: Disposable): Boolean

    /**
     * Create and return a new disposable unless this is a restarted request.
     */
    fun getDisposable(job: Deferred<ImageResult>): Disposable

    /** Cancel any in progress work and detach currentRequestDelegate from this view. */
    fun dispose()

    /** Return the completed value of the latest job if it has completed. Else, return 'null'. */
    fun getResult(): ImageResult?

    fun restart()

    fun getRequest(): ImageRequest?

    fun getSketch(): Sketch?
}