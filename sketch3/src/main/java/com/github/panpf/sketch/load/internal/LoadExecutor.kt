package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ExecuteResult
import com.github.panpf.sketch.common.Listener
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.internal.ListenerDelegate
import com.github.panpf.sketch.load.LoadData
import com.github.panpf.sketch.load.LoadRequest
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: LoadRequest,
        listener: Listener<LoadRequest, LoadData>?,
        extras: RequestExtras<LoadRequest, LoadData>?,
    ): ExecuteResult<LoadData> {
        val listenerDelegate = listener?.run {
            ListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: LoadData = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
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