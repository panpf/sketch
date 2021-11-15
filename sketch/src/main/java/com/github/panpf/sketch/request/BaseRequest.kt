/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Configuration
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.uri.UriModel

abstract class BaseRequest internal constructor(
    val sketch: Sketch,
    val uri: String,
    val uriModel: UriModel,
    val key: String,
    val logName: String
) : Runnable {

    private val resultShareManager: ResultShareManager = sketch.configuration.resultShareManager

    val diskCacheKey: String by lazy { uriModel.getDiskCacheKey(uri) }
    var status: Status? = null
        private set
    var errorCause: ErrorCause? = null
        private set
    var cancelCause: CancelCause? = null
        private set
    private var runStatus: RunStatus? = null
    var isSync = false
    val context: Context
        get() = sketch.configuration.context
    val configuration: Configuration
        get() = sketch.configuration

    fun setStatus(status: Status) {
        this.status = status
    }

    protected fun setErrorCause(cause: ErrorCause) {
        errorCause = cause
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(logName, "Request error. %s. %s. %s", cause.name, threadName, key)
        }
    }

    protected fun setCancelCause(cause: CancelCause) {
        if (!isFinished) {
            cancelCause = cause
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(logName, "Request cancel. %s. %s. %s", cause.name, threadName, key)
            }
        }
    }

    val isFinished: Boolean
        get() = status == Status.COMPLETED || status == Status.CANCELED || status == Status.FAILED
    open val isCanceled: Boolean
        get() = status == Status.CANCELED

    // todo 作为结果的一种传入 finished 方法处理
    protected open fun doError(errorCause: ErrorCause) {
        setErrorCause(errorCause)
        setStatus(Status.FAILED)
    }

    // todo 作为结果的一种传入 finished 方法处理
    protected open fun doCancel(cancelCause: CancelCause) {
        setCancelCause(cancelCause)
        setStatus(Status.CANCELED)
    }

    /**
     * Cancel Request
     *
     * @return false：request finished
     */
    fun cancel(cancelCause: CancelCause): Boolean {
        return if (!isFinished) {
            doCancel(cancelCause)
            true
        } else {
            false
        }
    }

    val threadName: String
        get() = Thread.currentThread().name

    override fun run() {
        val runStatus = runStatus
        if (runStatus == RunStatus.DISPATCH) {
            setStatus(Status.START_DISPATCH)
            val dispatchResult = runDispatch()
            if (dispatchResult is DownloadSuccessResult) {
                val downloadResult = dispatchResult.result
                onRunDownloadFinished(downloadResult)
                if (this is DownloadRequest) {
                    resultShareManager.unregisterDownloadShareProvider(this)
                }
            } else if (dispatchResult is RunDownloadResult) {
                submitDownload()
            } else if (dispatchResult is RunLoadResult) {
                submitLoad()
            }
        } else if (runStatus == RunStatus.DOWNLOAD) {
            setStatus(Status.START_DOWNLOAD)
            val downloadResult = runDownload()
            onRunDownloadFinished(downloadResult)
            if (this is DownloadRequest) {
                resultShareManager.unregisterDownloadShareProvider(this)
            }
        } else if (runStatus == RunStatus.LOAD) {
            setStatus(Status.START_LOAD)
            onRunLoadFinished(runLoad())
            if (this is DisplayRequest) {
                resultShareManager.unregisterDisplayResultShareProvider(this)
            }
        } else {
            SLog.emf(logName, "Unknown runStatus: %s", runStatus?.name!!)
        }
    }

    fun submitDispatch() {
        runStatus = RunStatus.DISPATCH
        if (isSync) {
            run()
        } else {
            setStatus(Status.WAIT_DISPATCH)
            configuration.executor.submitDispatch(this)
        }
    }

    fun submitDownload() {
        runStatus = RunStatus.DOWNLOAD
        if (isSync) {
            run()
        } else {
            if (this is DownloadRequest && this.canUseDownloadShare()) {
                val downloadRequest = this
                if (!resultShareManager.requestAttachDownloadShare(downloadRequest)) {
                    resultShareManager.registerDownloadShareProvider(downloadRequest)
                    setStatus(Status.WAIT_DOWNLOAD)
                    configuration.executor.submitDownload(this)
                }
            } else {
                setStatus(Status.WAIT_DOWNLOAD)
                configuration.executor.submitDownload(this)
            }
        }
    }

    fun submitLoad() {
        runStatus = RunStatus.LOAD
        if (isSync) {
            run()
        } else {
            if (this is DisplayRequest && this.canUseDisplayShare()) {
                val displayRequest = this
                if (!resultShareManager.requestAttachDisplayShare(displayRequest)) {
                    resultShareManager.registerDisplayResultShareProvider(displayRequest)
                    setStatus(Status.WAIT_LOAD)
                    configuration.executor.submitLoad(this)
                }
            } else {
                setStatus(Status.WAIT_LOAD)
                configuration.executor.submitLoad(this)
            }
        }
    }

    @WorkerThread
    abstract fun runDispatch(): DispatchResult?

    @WorkerThread
    abstract fun runDownload(): DownloadResult?

    @WorkerThread
    abstract fun onRunDownloadFinished(result: DownloadResult?)

    @WorkerThread
    abstract fun runLoad(): LoadResult?

    @WorkerThread
    abstract fun onRunLoadFinished(result: LoadResult?)
    fun postToMainRunUpdateProgress(totalLength: Int, completedLength: Int) {
        CallbackHandler.postRunUpdateProgress(this, totalLength, completedLength)
    }

    @UiThread
    abstract fun runUpdateProgressInMain(totalLength: Int, completedLength: Int)

    @AnyThread
    protected open fun postRunCompleted() {
        CallbackHandler.postRunCompleted(this@BaseRequest)
    }

    @UiThread
    abstract fun runCompletedInMain()

    @AnyThread
    protected open fun postToMainRunError() {
        CallbackHandler.postRunError(this)
    }

    @UiThread
    abstract fun runErrorInMain()

    @AnyThread
    fun postToMainRunCanceled() {
        CallbackHandler.postRunCanceled(this)
    }

    @UiThread
    abstract fun runCanceledInMain()
    fun updateProgress(totalLength: Int, completedLength: Int) {
        onUpdateProgress(totalLength, completedLength)
        if (this is DownloadRequest) {
            resultShareManager.updateDownloadProgress(this, totalLength, completedLength)
        }
    }

    abstract fun onUpdateProgress(totalLength: Int, completedLength: Int)

    private enum class RunStatus {
        DISPATCH, LOAD, DOWNLOAD
    }

    enum class Status {
        WAIT_DISPATCH,
        START_DISPATCH,
        INTERCEPT_LOCAL_TASK,
        WAIT_DOWNLOAD,
        START_DOWNLOAD,
        CHECK_DISK_CACHE,
        CONNECTING,
        READ_DATA,
        WAIT_LOAD,
        START_LOAD,
        CHECK_MEMORY_CACHE,
        DECODING,
        PROCESSING,
        WAIT_DISPLAY,
        COMPLETED,
        FAILED,
        CANCELED
    }
}