package com.github.panpf.sketch.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.download.DownloadErrorResult
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.download.DownloadResult
import com.github.panpf.sketch.download.DownloadSuccessResult
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(request: DownloadRequest): DownloadResult {
        val listenerDelegate = request.listener?.run {
            DownloadListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: DownloadResult = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request)

            if (listenerDelegate != null) {
                when (result) {
                    is DownloadSuccessResult -> {
                        listenerDelegate.onSuccess(request, result.data)
                    }
                    is DownloadErrorResult -> {
                        listenerDelegate.onError(request, result.throwable)
                    }
                }
            }
            return result
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                listenerDelegate?.onError(request, throwable)
                return DownloadErrorResult(throwable)
            }
        }
    }
}