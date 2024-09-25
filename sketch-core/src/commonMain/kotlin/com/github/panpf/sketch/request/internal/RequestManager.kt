/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.ReusableDisposable
import com.github.panpf.sketch.util.getCompletedOrNull
import com.github.panpf.sketch.util.isMainThread
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Responsible for storing current requests and calling back events such as attach, detach.
 *
 * @see com.github.panpf.sketch.request.internal.BaseRequestManager
 * @see com.github.panpf.sketch.request.internal.ViewRequestManager
 * @see com.github.panpf.sketch.request.internal.ComposeRequestManager
 */
interface RequestManager {

    /**
     * Attach [requestDelegate] to this view and cancel the old request.
     */
    @MainThread
    fun setRequest(requestDelegate: RequestDelegate?)

    /**
     * Return 'true' if [disposable] is not attached to this view.
     */
    fun isDisposed(disposable: Disposable): Boolean

    /**
     * Create and return a new disposable unless this is a restarted request.
     */
    fun getDisposable(job: Deferred<ImageResult>): Disposable

    /**
     * Cancel any in progress work and detach currentRequestDelegate from this view.
     */
    fun dispose()

    /**
     * Return the completed value of the latest job if it has completed. Else, return 'null'.
     */
    fun getResult(): ImageResult?

    /**
     * Restart the current request
     */
    fun restart()

    /**
     * Return the current request
     */
    fun getRequest(): ImageRequest?

    /**
     * Return the current sketch
     */
    fun getSketch(): Sketch?
}

/**
 * Base implementation of [RequestManager] that handles the current request and request lifecycle.
 *
 * @see com.github.panpf.sketch.request.internal.ViewRequestManager
 * @see com.github.panpf.sketch.request.internal.ComposeRequestManager
 */
open class BaseRequestManager : RequestManager {

    private val lock = SynchronizedObject()

    // The disposable for the current request attached to this view.
    private var currentDisposable: ReusableDisposable? = null

    // A pending operation that is posting to the main thread to clear the current request.
    private var pendingClear: Job? = null

    // Only accessed from the main thread.
    protected var currentRequestDelegate: RequestDelegate? = null
    private var isRestart = false

    @MainThread
    override fun setRequest(requestDelegate: RequestDelegate?) {
        currentRequestDelegate?.dispose()
        currentRequestDelegate = requestDelegate
        callbackAttachedState()
    }

    override fun isDisposed(disposable: Disposable): Boolean {
        return synchronized(lock) { disposable !== currentDisposable }
    }

    override fun getDisposable(job: Deferred<ImageResult>): ReusableDisposable =
        synchronized(lock) {
            // If this is a restarted request, update the current disposable and return it.
            val disposable = currentDisposable
            if (disposable != null && isMainThread() && isRestart) {
                isRestart = false
                disposable.job = job
                return disposable
            }

            // Cancel any pending clears since they were for the previous request.
            pendingClear?.cancel()
            pendingClear = null

            // Create a new disposable as this is a new request.
            return ReusableDisposable(this@BaseRequestManager, job).also {
                currentDisposable = it
            }
        }

    @OptIn(DelicateCoroutinesApi::class)
    override fun dispose() = synchronized(lock) {
        pendingClear?.cancel()
        pendingClear = GlobalScope.launch(Dispatchers.Main.immediate) {
            setRequest(null)
        }
        currentDisposable = null
    }

    override fun getResult(): ImageResult? = synchronized(lock) {
        return currentDisposable?.job?.getCompletedOrNull()
    }

    override fun restart() = synchronized(lock) {
        val requestDelegate = currentRequestDelegate ?: return@synchronized

        // As this is called from the main thread, isRestart will
        // be cleared synchronously as part of request.restart().
        isRestart = true
        requestDelegate.sketch.enqueue(requestDelegate.initialRequest)
    }

    override fun getRequest(): ImageRequest? = synchronized(lock) {
        return currentRequestDelegate?.initialRequest
    }

    override fun getSketch(): Sketch? = synchronized(lock) {
        return currentRequestDelegate?.sketch
    }

    open fun isAttached(): Boolean {    // TODO change to abstract
        return true
    }

    protected fun callbackAttachedState() {
        val currentRequestDelegate = currentRequestDelegate
        if (currentRequestDelegate is AttachObserver) {
            val isAttached = isAttached()
            currentRequestDelegate.onAttachedChanged(isAttached)
        }
    }
}