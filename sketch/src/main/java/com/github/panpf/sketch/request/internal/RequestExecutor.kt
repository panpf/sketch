package com.github.panpf.sketch.request.internal

import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.tryToResizeDrawable
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.transition.TransitionTarget
import com.github.panpf.sketch.util.OtherException
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.awaitStarted
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.job
import kotlin.coroutines.coroutineContext

class RequestExecutor {

    companion object {
        const val MODULE = "RequestExecutor"
    }

    @MainThread
    suspend fun execute(
        sketch: Sketch,
        originRequest: ImageRequest,
        enqueue: Boolean
    ): ImageResult {
        requiredMainThread()
        var request: ImageRequest = sketch.globalImageOptions
            ?.let { originRequest.newBuilder().global(it).build() }
            ?: originRequest
        // Wrap the request to manage its lifecycle.
        val requestDelegate = requestDelegate(sketch, request, coroutineContext.job)
        requestDelegate.assertActive()

        val target = request.target
        val requestExtras = RequestExtras()
        try {
            if (request.uriString.isEmpty() || request.uriString.isBlank()) {
                throw UriInvalidException(request, "Request uri is empty or blank")
            }

            // Set up the request's lifecycle observers. Cancel the request when destroy
            requestDelegate.start()

            // Enqueued requests suspend until the lifecycle is started.
            if (enqueue) {
                request.lifecycle.awaitStarted()
            }

            if (request.resizeSize == null) {
                val size = request.resizeSizeResolver?.size()
                if (size != null) {
                    request = request.newRequest {
                        resizeSize(size)
                    }
                }
            }

            onStart(sketch, request)

            val data = RequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestExtras = requestExtras,
                interceptors = sketch.requestInterceptors,
                index = 0,
            ).proceed(request)

            val successResult = when (data) {
                is DisplayData -> DisplayResult.Success(
                    request,
                    data.drawable.tryToResizeDrawable(sketch, request),
                    data.imageInfo,
                    data.dataFrom
                )
                is LoadData -> LoadResult.Success(
                    request,
                    data.bitmap,
                    data.imageInfo,
                    data.dataFrom
                )
                is DownloadData -> DownloadResult.Success(request, data, data.dataFrom)
                else -> throw UnsupportedOperationException("Unsupported ImageData: ${data::class.java}")
            }
            onSuccess(sketch, request, target, successResult)
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                onCancel(sketch, request)
                throw throwable
            } else {
                throwable.printStackTrace()
                val exception = throwable.asOrNull<SketchException>()
                    ?: OtherException(request, throwable.toString(), throwable)
                val errorResult = when (request) {
                    is DisplayRequest -> {
                        val errorDrawable = request.errorImage
                            ?.getDrawable(sketch, request, exception)
                            ?.tryToResizeDrawable(sketch, request)
                            ?: request.placeholderImage
                                ?.getDrawable(sketch, request, exception)
                                ?.tryToResizeDrawable(sketch, request)
                        DisplayResult.Error(request, errorDrawable, exception)
                    }
                    is LoadRequest -> LoadResult.Error(request, exception)
                    is DownloadRequest -> DownloadResult.Error(request, exception)
                    else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${request::class.java}")
                }

                onError(sketch, request, target, errorResult)
                return errorResult
            }
        } finally {
            requestExtras.getCountDrawablePendingManagerKey()?.let {
                sketch.countDrawablePendingManager.complete("RequestCompleted", it)
            }
            requestDelegate.finish()
        }
    }

    private fun onStart(sketch: Sketch, request: ImageRequest) {
        sketch.logger.d(MODULE) {
            "Request started. ${request.key}"
        }
        request.listener?.onStart(request)
    }

    private fun onSuccess(
        sketch: Sketch,
        request: ImageRequest,
        target: Target?,
        result: ImageResult.Success
    ) {
        sketch.logger.d(MODULE) {
            if (result is DisplayResult.Success) {
                "Request Successful. ${result.drawable}. ${request.key}"
            } else {
                "Request Successful. ${request.uriString}"
            }
        }
        when {
            target is DisplayTarget && result is DisplayResult.Success -> {
                transition(target, result) {
                    target.onSuccess(result.drawable)
                }
            }
            target is LoadTarget && result is LoadResult.Success -> {
                target.onSuccess(result.bitmap)
            }
            target is DownloadTarget && result is DownloadResult.Success -> {
                target.onSuccess(result.data)
            }
        }
        request.listener?.onSuccess(request, result)
    }

    private fun onError(
        sketch: Sketch,
        request: ImageRequest,
        target: Target?,
        result: ImageResult.Error
    ) {
        sketch.logger.e(MODULE, result.exception) {
            "Request failed. ${result.exception.message}. ${request.key}"
        }
        when {
            target is DisplayTarget && result is DisplayResult.Error -> {
                transition(target, result) {
                    target.onError(result.drawable)
                }
            }
            target is LoadTarget && result is LoadResult.Error -> {
                target.onError(result.exception)
            }
            target is DownloadTarget && result is DownloadResult.Error -> {
                target.onError(result.exception)
            }
        }
        request.listener?.onError(request, result)
    }

    private fun onCancel(sketch: Sketch, request: ImageRequest) {
        sketch.logger.d(MODULE) {
            "Request canceled. ${request.key}"
        }
        request.listener?.onCancel(request)
    }

    private fun transition(
        target: Target?,
        result: DisplayResult,
        setDrawable: () -> Unit
    ) {
        if (target !is TransitionTarget) {
            setDrawable()
            return
        }

        if (result.drawable == null) {
            setDrawable()
            return
        }

        val fitScale = target.asOrNull<ViewTarget<View>>()?.view.asOrNull<ImageView>()?.fitScale ?: true
        val transition = result.request.transition?.create(target, result, fitScale)
        if (transition == null) {
            setDrawable()
            return
        }

        transition.transition()
    }

    private val ImageView.fitScale: Boolean
        get() = when (scaleType) {
            ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER_INSIDE -> true
            else -> false
        }
}