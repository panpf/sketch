package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transition.TransitionTarget
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.job
import kotlin.coroutines.coroutineContext

class DisplayExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "DisplayExecutor"
    }

    @MainThread
    suspend fun execute(request: DisplayRequest): DisplayResult {
        requiredMainThread()
        // Wrap the request to manage its lifecycle.
        val requestDelegate = requestDelegate(sketch, request, coroutineContext.job)
        requestDelegate.assertActive()
        val target = request.target
        val requestExtras = RequestExtras()

        try {
            if (request.uriString.isEmpty() || request.uriString.isBlank()) {
                throw UriEmptyException(request)
            }

            // Set up the request's lifecycle observers.
            requestDelegate.start()
            onStart(request)

            val newRequest = if (request.resizeSize == null) {
                val newResizeSize = request.resizeSizeResolver.size()
                if (newResizeSize != null) {
                    request.newDisplayRequest { resizeSize(newResizeSize) }
                } else {
                    request
                }
            } else {
                request
            }

            val data = DisplayInterceptorChain(
                initialRequest = request,
                interceptors = sketch.displayInterceptors,
                index = 0,
                sketch = sketch,
                request = newRequest,
                requestExtras = requestExtras,
            ).proceed(newRequest)

            val successResult = Success(
                request,
                data.drawable,
                data.imageInfo,
                data.dataFrom
            )
            onSuccess(request, target, successResult)
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                val exception = throwable.asOrNull<SketchException>()
                    ?: SketchException(request, throwable.toString(), throwable)
                val errorDrawable = request.errorImage?.getDrawable(sketch, request, exception)
                    ?: request.placeholderImage?.getDrawable(sketch, request, null)
                val errorResult = Error(request, errorDrawable, exception)
                onError(request, target, errorResult)
                return errorResult
            }
        } finally {
            requestExtras.getCountDrawablePendingManagerKey()?.let {
                sketch.countDrawablePendingManager.complete("RequestCompleted", it)
            }
            requestDelegate.complete()
        }
    }

    private fun onStart(request: DisplayRequest) {
        sketch.logger.d(MODULE) {
            "Request started. ${request.key}"
        }
        request.listener?.onStart(request)
    }

    private fun onSuccess(request: DisplayRequest, target: Target, result: Success) {
        sketch.logger.d(MODULE) {
            "Request Successful. ${result.drawable}. ${request.key}"
        }
        transition(target, result) {
            target.onSuccess(result.drawable)
        }
        request.listener?.onSuccess(request, result)
    }

    private fun onError(request: DisplayRequest, target: Target, result: Error) {
        val message = "Request failed. ${result.exception.message}. ${request.key}"
        sketch.logger.e(MODULE, result.exception, message)
        transition(target, result) {
            target.onError(result.drawable)
        }
        request.listener?.onError(request, result)
    }

    private fun onCancel(request: DisplayRequest) {
        sketch.logger.d(MODULE) {
            "Request canceled. ${request.key}"
        }
        request.listener?.onCancel(request)
    }

    private fun transition(target: Target?, result: DisplayResult, setDrawable: () -> Unit) {
        if (target !is TransitionTarget) {
            setDrawable()
            return
        }

        val transition = result.request.transition?.create(target, result)
        if (transition == null) {
            setDrawable()
            return
        }

        transition.transition()
    }
}