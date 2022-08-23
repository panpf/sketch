/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request.internal

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.tryToResizeDrawable
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.UnknownException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.awaitStarted
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.job
import kotlin.coroutines.coroutineContext

class RequestExecutor {

    companion object {
        const val MODULE = "RequestExecutor"
    }

    @MainThread
    suspend fun execute(sketch: Sketch, request: ImageRequest, enqueue: Boolean): ImageResult {
        requiredMainThread()

        val requestContext = RequestContext(request)

        // Wrap the request to manage its lifecycle.
        val requestDelegate =
            requestDelegate(sketch, requestContext.lastRequest, coroutineContext.job)
        requestDelegate.assertActive()

        try {
            val uriString = requestContext.lastRequest.uriString
            if (uriString.isEmpty() || uriString.isBlank()) {
                throw UriInvalidException("Request uri is empty or blank")
            }

            // Set up the request's lifecycle observers. Cancel the request when destroy
            requestDelegate.start()

            // Enqueued requests suspend until the lifecycle is started.
            if (enqueue) {
                requestContext.lastRequest.lifecycle.awaitStarted()
            }

            // globalImageOptions
            sketch.globalImageOptions?.let {
                val defaultOptions = requestContext.lastRequest.defaultOptions
                if (defaultOptions !== it) {
                    val newDefaultOptions = defaultOptions?.merged(it) ?: it
                    requestContext.addRequest(
                        requestContext.lastRequest.newBuilder().default(newDefaultOptions).build()
                    )
                }
            }

            // resolve resize size
            if (requestContext.lastRequest.resizeSize == null) {
                val resizeSize = requestContext.lastRequest.resizeSizeResolver?.size()
                if (resizeSize != null) {
                    requestContext.addRequest(requestContext.lastRequest.newRequest {
                        resizeSize(resizeSize)
                    })
                }
            }

            onStart(sketch, requestContext.lastRequest)

            val imageData: ImageData = RequestInterceptorChain(
                sketch = sketch,
                initialRequest = requestContext.lastRequest,
                request = requestContext.lastRequest,
                requestContext = requestContext,
                interceptors = sketch.components.requestInterceptorList,
                index = 0,
            ).proceed(requestContext.lastRequest)

            val lastRequest: ImageRequest = requestContext.lastRequest
            val successResult: ImageResult.Success = when {
                lastRequest is DisplayRequest && imageData is DisplayData -> DisplayResult.Success(
                    request = lastRequest,
                    drawable = imageData.drawable.tryToResizeDrawable(lastRequest),
                    imageInfo = imageData.imageInfo,
                    dataFrom = imageData.dataFrom,
                    transformedList = imageData.transformedList
                )
                lastRequest is LoadRequest && imageData is LoadData -> LoadResult.Success(
                    request = lastRequest,
                    bitmap = imageData.bitmap,
                    imageInfo = imageData.imageInfo,
                    dataFrom = imageData.dataFrom,
                    transformedList = imageData.transformedList
                )
                lastRequest is DownloadRequest && imageData is DownloadData -> DownloadResult.Success(
                    lastRequest, imageData
                )
                else -> throw UnsupportedOperationException("Unsupported ImageData: ${imageData::class.java}")
            }
            onSuccess(sketch, lastRequest, successResult)
            return successResult
        } catch (throwable: Throwable) {
            val lastRequest = requestContext.lastRequest
            if (throwable is CancellationException) {
                onCancel(sketch, lastRequest)
                throw throwable
            } else {
                if (throwable !is DepthException) {
                    throwable.printStackTrace()
                }
                val exception: SketchException = throwable.asOrNull<SketchException>()
                    ?: UnknownException(throwable.toString(), throwable)
                val errorResult: ImageResult.Error = when (lastRequest) {
                    is DisplayRequest -> DisplayResult.Error(
                        lastRequest, getErrorDrawable(sketch, lastRequest, exception), exception
                    )
                    is LoadRequest -> LoadResult.Error(lastRequest, exception)
                    is DownloadRequest -> DownloadResult.Error(lastRequest, exception)
                    else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${lastRequest::class.java}")
                }

                onError(sketch, lastRequest, errorResult)
                return errorResult
            }
        } finally {
            requestContext.completeCountDrawable("RequestCompleted")
            requestDelegate.finish()
        }
    }

    @MainThread
    private fun onStart(sketch: Sketch, request: ImageRequest) {
        request.listener?.onStart(request)
        sketch.logger.d(MODULE) {
            "Request started. ${request.key}"
        }
    }

    @MainThread
    private fun onSuccess(sketch: Sketch, request: ImageRequest, result: ImageResult.Success) {
        val target = request.target
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
        sketch.logger.d(MODULE) {
            if (result is DisplayResult.Success) {
                "Request Successful. ${result.drawable}. ${request.key}"
            } else {
                "Request Successful. ${request.uriString}"
            }
        }
    }

    @MainThread
    private fun onError(sketch: Sketch, request: ImageRequest, result: ImageResult.Error) {
        val target = request.target
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
        if (result.exception is DepthException) {
            sketch.logger.w(MODULE) {
                "Request failed. ${result.exception.message}. ${request.key}"
            }
        } else {
            sketch.logger.e(MODULE, result.exception) {
                "Request failed. ${result.exception.message}. ${request.key}"
            }
        }
    }

    @MainThread
    private fun onCancel(sketch: Sketch, request: ImageRequest) {
        sketch.logger.d(MODULE) {
            "Request canceled. ${request.key}"
        }
        request.listener?.onCancel(request)
    }

    @MainThread
    private fun transition(
        target: Target?,
        result: DisplayResult,
        setDrawable: () -> Unit
    ) {
        if (target !is TransitionDisplayTarget) {
            setDrawable()
            return
        }

        if (result.drawable == null) {
            setDrawable()
            return
        }

        val fitScale =
            target.asOrNull<ViewDisplayTarget<View>>()?.view.asOrNull<ImageView>()?.fitScale ?: true
        val transitionFactory = result.request.transitionFactory?.create(target, result, fitScale)
        if (transitionFactory == null) {
            setDrawable()
            return
        }

        transitionFactory.transition()
    }

    private fun getErrorDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException
    ): Drawable? = request.error?.let {
        it.getDrawable(sketch, request, exception)?.tryToResizeDrawable(request)
    } ?: request.placeholder?.let {
        it.getDrawable(sketch, request, exception)?.tryToResizeDrawable(request)
    }
}