package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import kotlinx.coroutines.CancellationException

class DisplayExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: DisplayRequest,
        lifecycleListener: Listener<DisplayRequest, DisplayResult>?,
        httpFetchProgressListener: ProgressListener<DisplayRequest>?,
    ): ExecuteResult<DisplayResult> {
        val listenerDelegate = lifecycleListener?.run {
            ListenerDelegate(this)
        }
        val progressListenerDelegate = httpFetchProgressListener?.run {
            ProgressListenerDelegate(this)
        }

        try {
            // todo 过滤重复请求
            listenerDelegate?.onStart(request)

            val result: DisplayResult = DisplayInterceptorChain(
                initialRequest = request,
                interceptors = sketch.displayInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request, progressListenerDelegate)

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