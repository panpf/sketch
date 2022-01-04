package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(request: LoadRequest): ExecuteResult<LoadResult> {
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val loadResult = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request)

            listenerDelegate?.onSuccess(request, loadResult)
            return ExecuteResult.Success(loadResult)
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