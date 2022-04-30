package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.drawable.internal.toResizeDrawable
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
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transition.TransitionTarget
import com.github.panpf.sketch.util.OtherException
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.job
import kotlin.coroutines.coroutineContext

class RequestExecutor {

    companion object {
        const val MODULE = "RequestExecutor"
    }

    @MainThread
    suspend fun execute(request: ImageRequest): ImageResult {
        requiredMainThread()
        // Wrap the request to manage its lifecycle.
        val requestDelegate = requestDelegate(request, coroutineContext.job)
        requestDelegate.assertActive()
        val target = request.target
        val requestExtras = RequestExtras()
        var resize: Resize? = null

        try {
            if (request.uriString.isEmpty() || request.uriString.isBlank()) {
                throw UriInvalidException(request, "Request uri is empty or blank")
            }

            // Set up the request's lifecycle observers.
            requestDelegate.start()
            onStart(request)

            val newRequest = if (request.resizeSize == null) {
                val newResizeSize = request.resizeSizeResolver.size()
                if (newResizeSize != null) {
                    request.newRequest { resizeSize(newResizeSize) }
                } else {
                    request
                }
            } else {
                request
            }
            resize = newRequest.resize

            val data = RequestInterceptorChain(
                initialRequest = request,
                interceptors = request.sketch.requestInterceptors,
                index = 0,
                request = newRequest,
                requestExtras = requestExtras,
            ).proceed(newRequest)

            val successResult = when (data) {
                is DisplayData -> DisplayResult.Success(
                    request,
                    data.drawable.let {
                        if (newRequest.resizeApplyToDrawable == true) {
                            it.toResizeDrawable(request.sketch, resize)
                        } else {
                            it
                        }
                    },
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
            onSuccess(request, target, successResult)
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                val exception = throwable.asOrNull<SketchException>()
                    ?: OtherException(request, throwable.toString(), throwable)
                val errorResult = when (request) {
                    is DisplayRequest -> {
                        val errorDrawable = request.errorImage
                            ?.getDrawable(request, exception)
                            ?.let {
                                if (request.resizeApplyToDrawable == true) {
                                    it.toResizeDrawable(request.sketch, resize)
                                } else {
                                    it
                                }
                            }
                        DisplayResult.Error(
                            request,
                            errorDrawable,
                            exception
                        )
                    }
                    is LoadRequest -> LoadResult.Error(request, exception)
                    is DownloadRequest -> DownloadResult.Error(request, exception)
                    else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${request::class.java}")
                }

                onError(request, target, errorResult)
                return errorResult
            }
        } finally {
            requestExtras.getCountDrawablePendingManagerKey()?.let {
                request.sketch.countDrawablePendingManager.complete("RequestCompleted", it)
            }
            requestDelegate.complete()
        }
    }

    private fun onStart(request: ImageRequest) {
        request.sketch.logger.d(MODULE) {
            "Request started. ${request.key}"
        }
        request.listener?.onStart(request)
    }

    private fun onSuccess(request: ImageRequest, target: Target?, result: ImageResult.Success) {
        request.sketch.logger.d(MODULE) {
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

    private fun onError(request: ImageRequest, target: Target?, result: ImageResult.Error) {
        request.sketch.logger.e(MODULE, result.exception) {
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

    private fun onCancel(request: ImageRequest) {
        request.sketch.logger.d(MODULE) {
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

        val transition = result.request.transition?.create(target, result)
        if (transition == null) {
            setDrawable()
            return
        }

        transition.transition()
    }
}