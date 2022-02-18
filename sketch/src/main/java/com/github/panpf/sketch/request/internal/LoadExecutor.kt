package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "LoadExecutor"
    }

    @MainThread
    suspend fun execute(request: LoadRequest): LoadResult {
        requiredMainThread()
        val requestExtras = RequestExtras()
        try {
            onStart(request)

            val newRequest = if (request.resizeSize == null) {
                val newResizeSize = request.resizeSizeResolver.size()
                if (newResizeSize != null) {
                    request.newLoadRequest { resizeSize(newResizeSize) }
                } else {
                    request
                }
            } else {
                request
            }

            val loadData = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
                index = 0,
                sketch = sketch,
                request = newRequest,
                requestExtras = requestExtras,
            ).proceed(newRequest)

            val successResult =
                LoadResult.Success(request, loadData.bitmap, loadData.imageInfo, loadData.dataFrom)
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
                val errorResult = LoadResult.Error(request, exception)
                onError(request, errorResult)
                return errorResult
            }
        }
    }

    private fun onStart(request: LoadRequest) {
        sketch.logger.d(MODULE) {
            "Request started. ${request.uriString}"
        }
        request.listener?.onStart(request)
    }

    private fun onSuccess(request: LoadRequest, result: LoadResult.Success) {
        sketch.logger.d(MODULE) {
            "Request Successful. ${request.uriString}"
        }
        request.listener?.onSuccess(request, result)
    }

    private fun onCancel(request: LoadRequest) {
        sketch.logger.d(MODULE) {
            "Request canceled. ${request.uriString}"
        }
        request.listener?.onCancel(request)
    }

    private fun onError(request: LoadRequest, result: LoadResult.Error) {
        sketch.logger.e(
            MODULE,
            result.exception,
            "Request failed. ${result.exception.message} .${request.key}"
        )
        request.listener?.onError(request, result)
    }
}