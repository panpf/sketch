package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: DownloadRequest,
        lifecycleListener: Listener<DownloadRequest, DownloadResult>?,
        httpFetchProgressListener: ProgressListener<DownloadRequest>?,
    ): ExecuteResult<DownloadResult> {
        val listenerDelegate = lifecycleListener?.run {
            ListenerDelegate(this)
        }
        val progressListenerDelegate = httpFetchProgressListener?.run {
            ProgressListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val downloadResult = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request, progressListenerDelegate)

            listenerDelegate?.onSuccess(request, downloadResult)
            return ExecuteResult.Success(downloadResult)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                listenerDelegate?.onError(request, throwable)
                return ExecuteResult.Error(throwable)
            }
        }
    }
}