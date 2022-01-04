package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "LoadExecutor"
    }

    @WorkerThread
    suspend fun execute(request: LoadRequest): ExecuteResult<LoadResult> {
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }

        try {
            sketch.logger.d(MODULE) {
                "Request started. ${request.uriString}"
            }
            listenerDelegate?.onStart(request)

            val loadResult = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request)

            listenerDelegate?.onSuccess(request, loadResult)
            sketch.logger.d(MODULE) {
                "Request Successful. ${request.uriString}"
            }
            return ExecuteResult.Success(loadResult)
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                listenerDelegate?.onCancel(request)
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.uriString}"
                }
                throw throwable
            } else {
                throwable.printStackTrace()
                listenerDelegate?.onError(request, throwable)
                sketch.logger.e(MODULE, throwable, throwable.message.orEmpty())
                return ExecuteResult.Error(throwable)
            }
        }
    }
}