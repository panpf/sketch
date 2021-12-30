package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.ListenerInfo
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
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
            // todo 过滤重复加载
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
                throwable.printStackTrace()
                listenerDelegate?.onError(request, throwable)
                return ExecuteResult.Error(throwable)
            }
        }
    }
}