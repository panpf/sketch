package com.github.panpf.sketch.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ExecuteResult
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.internal.ListenerDelegate
import com.github.panpf.sketch.download.DownloadData
import com.github.panpf.sketch.download.DownloadRequest
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: DownloadRequest,
        extras: RequestExtras<DownloadRequest, DownloadData>?,
    ): ExecuteResult<DownloadData> {
        val listenerDelegate = extras?.listener?.run {
            ListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: DownloadData = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request, extras)

            listenerDelegate?.onSuccess(request, result)
            return ExecuteResult.Success(result)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                listenerDelegate?.onError(request, throwable)
                return ExecuteResult.Error(throwable)
            }
        }
    }
}