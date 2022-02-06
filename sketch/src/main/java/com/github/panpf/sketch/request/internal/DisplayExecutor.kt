package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class DisplayExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "DisplayExecutor"
    }

    @MainThread
    suspend fun execute(request: DisplayRequest): DisplayResult {
        // Wrap the request to manage its lifecycle.
        val requestDelegate = requestDelegate(sketch, request, coroutineContext.job)
        requestDelegate.assertActive()
        val target = request.target
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }

        try {
            if (request.uriString.isEmpty() || request.uriString.isBlank()) {
                throw UriEmptyException(request)
            }

            // Set up the request's lifecycle observers.
            requestDelegate.start()

            sketch.logger.d(MODULE) {
                "Request started. ${request.key}"
            }
            listenerDelegate?.onStart(request)

            val displayData = withContext(sketch.decodeTaskDispatcher) {
                DisplayInterceptorChain(
                    initialRequest = request,
                    interceptors = sketch.displayInterceptors,
                    index = 0,
                    sketch = sketch,
                    request = request,
                ).proceed(request)
            }

            // Successful
            withContext(Dispatchers.Main) {
                target.onSuccess(displayData.drawable)
            }
            val successResult = DisplayResult.Success(request, displayData)
            listenerDelegate?.onSuccess(request, successResult)
            sketch.logger.d(MODULE) {
                "Request Successful. ${request.key}"
            }
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                // Canceled
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.key}"
                }
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                // Exception
                throwable.printStackTrace()
                val message = "Request error. ${throwable.message}. ${request.key}"
                sketch.logger.e(MODULE, throwable, message)
                val exception = throwable.asOrNull<SketchException>()
                    ?: SketchException(request, null, throwable)
                val errorDrawable = request.errorImage?.getDrawable(sketch, request, exception)
                    ?: request.placeholderImage?.getDrawable(sketch, request, null)
                val errorResult = DisplayResult.Error(request, exception, errorDrawable)
                withContext(Dispatchers.Main) {
                    target.onError(errorDrawable)
                }
                listenerDelegate?.onError(request, errorResult)
                return errorResult
            }
        } finally {
            requestDelegate.complete()
        }
    }
}