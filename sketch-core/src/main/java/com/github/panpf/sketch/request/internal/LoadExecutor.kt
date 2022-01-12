package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.internal.DisplayExecutor.Companion
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "LoadExecutor"
    }

    @WorkerThread
    suspend fun execute(request: LoadRequest): LoadResult {
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }

        try {
            sketch.logger.d(MODULE) {
                "Request started. ${request.uriString}"
            }
            listenerDelegate?.onStart(request)

            val loadData = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request)

            sketch.logger.d(MODULE) {
                "Request Successful. ${request.uriString}"
            }
            val successResult = LoadResult.Success(request, loadData)
            listenerDelegate?.onSuccess(request, successResult)
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                listenerDelegate?.onCancel(request)
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.uriString}"
                }
                throw throwable
            } else {
                throwable.printStackTrace()
                sketch.logger.e(MODULE, throwable, "Request error. ${throwable.message} .${request.key}")
                val exception = throwable.asOrNull<SketchException>()
                    ?: SketchException(request, null, throwable)
                val errorResult = LoadResult.Error(request, exception)
                listenerDelegate?.onError(request, errorResult)
                return errorResult
            }
        }
    }
}