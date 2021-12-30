package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.ListenerInfo
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: DownloadRequest,
        listenerInfo: ListenerInfo<DownloadRequest, DownloadResult>?,
    ): ExecuteResult<DownloadResult> {
        val listenerDelegate = listenerInfo?.lifecycleListener?.run {
            ListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: DownloadResult = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request, listenerInfo)

            listenerDelegate?.onSuccess(request, result)
            return ExecuteResult.Success(result)
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