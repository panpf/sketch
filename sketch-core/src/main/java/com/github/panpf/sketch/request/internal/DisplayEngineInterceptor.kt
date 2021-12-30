package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult

class DisplayEngineInterceptor : Interceptor<DisplayRequest, DisplayResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DisplayRequest, DisplayResult>,
        httpFetchProgressListenerDelegate: ProgressListenerDelegate<DisplayRequest>?
    ): DisplayResult {
        val request = chain.request

        val loadRequest = request.toLoadRequest()
        val newProgressListenerDelegate = httpFetchProgressListenerDelegate?.let {
            ProgressListenerDelegate<LoadRequest> { _, it2, it3 ->
                it.progressListener.onUpdateProgress(request, it2, it3)
            }
        }
        val loadResult: LoadResult = LoadInterceptorChain(
            initialRequest = loadRequest,
            interceptors = sketch.loadInterceptors,
            index = 0,
            request = loadRequest,
        ).proceed(sketch, loadRequest, newProgressListenerDelegate)

        // todo memory cache
        // todo load
        TODO()
    }
}