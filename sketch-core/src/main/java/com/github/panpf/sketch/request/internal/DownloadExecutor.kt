package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ExecuteResult
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "DownloadExecutor"
    }

    @WorkerThread
    suspend fun execute(request: DownloadRequest): ExecuteResult<DownloadResult> {
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }

        try {
            sketch.logger.d(MODULE) {
                "Request started. ${request.uriString}"
            }
            listenerDelegate?.onStart(request)

            val downloadResult = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request)

            listenerDelegate?.onSuccess(request, downloadResult)
            sketch.logger.d(MODULE) {
                "Request Successful. ${request.uriString}"
            }
            return ExecuteResult.Success(downloadResult)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.uriString}"
                }
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                sketch.logger.e(MODULE, throwable, throwable.message.orEmpty())
                listenerDelegate?.onError(request, throwable)
                return ExecuteResult.Error(throwable)
            }
        }
    }
}