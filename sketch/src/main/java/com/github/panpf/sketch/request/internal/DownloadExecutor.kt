package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException

class DownloadExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "DownloadExecutor"
    }

    @MainThread
    suspend fun execute(request: DownloadRequest): DownloadResult {
        requiredMainThread()
        val requestExtras = RequestExtras()
        try {
            onStart(request)

            val downloadData = DownloadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                sketch = sketch,
                request = request,
                requestExtras = requestExtras,
            ).proceed(request)

            val successResult = DownloadResult.Success(request, downloadData, downloadData.dataFrom)
            onSuccess(request, successResult)
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                val exception = throwable.asOrNull<SketchException>()
                    ?: SketchException(request, throwable.toString(), throwable)
                val errorResult = DownloadResult.Error(request, exception)
                onError(request, errorResult)
                return errorResult
            }
        }
    }

    private fun onStart(request: DownloadRequest) {
        sketch.logger.d(MODULE) {
            "Request started. ${request.uriString}"
        }
        request.listener?.onStart(request)
    }

    private fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) {
        sketch.logger.d(MODULE) {
            "Request Successful. ${request.uriString}"
        }
        request.listener?.onSuccess(request, result)
    }

    private fun onCancel(request: DownloadRequest) {
        sketch.logger.d(MODULE) {
            "Request canceled. ${request.uriString}"
        }
        request.listener?.onCancel(request)
    }

    private fun onError(request: DownloadRequest, result: DownloadResult.Error) {
        sketch.logger.e(
            MODULE,
            result.exception,
            "Request failed. ${result.exception.message} .${request.key}"
        )
        request.listener?.onError(request, result)
    }
}