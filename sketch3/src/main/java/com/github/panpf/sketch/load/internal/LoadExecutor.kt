package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.Listener
import com.github.panpf.sketch.common.ProgressListener
import com.github.panpf.sketch.common.internal.ListenerDelegate
import com.github.panpf.sketch.load.*
import kotlinx.coroutines.CancellationException

class LoadExecutor(private val sketch: Sketch) {

    @WorkerThread
    suspend fun execute(
        request: LoadRequest,
        listener: Listener<LoadRequest, LoadData>?,
        httpFetchProgressListener: ProgressListener<ImageRequest>?,
    ): LoadResult {
        val listenerDelegate = listener?.run {
            ListenerDelegate(this)
        }

        try {
            listenerDelegate?.onStart(request)

            val result: LoadResult = LoadInterceptorChain(
                initialRequest = request,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = request,
            ).proceed(sketch, request, httpFetchProgressListener)

            if (listenerDelegate != null) {
                when (result) {
                    is LoadSuccessResult -> {
                        listenerDelegate.onSuccess(request, result.data)
                    }
                    is LoadErrorResult -> {
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
                return LoadErrorResult(throwable)
            }
        }
    }
}