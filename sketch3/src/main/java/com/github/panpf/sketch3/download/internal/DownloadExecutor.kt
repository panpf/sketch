package com.github.panpf.sketch3.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.download.DownloadErrorResult
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult
import com.github.panpf.sketch3.download.DownloadSuccessResult
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch3: Sketch3) {

    @WorkerThread
    suspend fun execute(request: DownloadRequest): DownloadResult {
        val listenerDelegate = request.listener?.run {
            DownloadListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: DownloadResult = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch3.downloadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch3, request)

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