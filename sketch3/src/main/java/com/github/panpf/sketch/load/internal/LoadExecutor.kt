package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ExecuteResult
import com.github.panpf.sketch.common.ListenerInfo
import com.github.panpf.sketch.common.internal.ListenerDelegate
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: LoadRequest,
        listenerInfo: ListenerInfo<LoadRequest, LoadResult>?,
    ): ExecuteResult<LoadResult> {
        val listenerDelegate = listenerInfo?.lifecycleListener?.run {
            ListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: LoadResult = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
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
                listenerDelegate?.onError(request, throwable)
                return ExecuteResult.Error(throwable)
            }
        }
    }
}