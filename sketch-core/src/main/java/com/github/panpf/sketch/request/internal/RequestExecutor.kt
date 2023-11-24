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
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.drawable.internal.tryToResizeDrawable
import com.github.panpf.sketch.request.DepthException
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
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.stateimage.internal.toSketchStateDrawable
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchException
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
        private const val uriEmptyMessage = "Request uri is empty or blank"
    }

    @MainThread
    suspend fun execute(sketch: Sketch, request: ImageRequest, enqueue: Boolean): ImageResult {
        requiredMainThread()

        // Wrap the request to manage its lifecycle.
        val lifecycle = request.lifecycleResolver.lifecycle()
        val requestDelegate = requestDelegate(sketch, request, lifecycle, coroutineContext.job)
        requestDelegate.assertActive()
        var requestContext: RequestContext? = null
        var firstRequestKey: String? = null

        try {
            // Set up the request's lifecycle observers. Cancel the request when destroy
            requestDelegate.start()

            // Enqueued requests suspend until the lifecycle is started.
            if (enqueue) {
                lifecycle.awaitStarted()
            }

            // resolve resize size
            val resizeSize = request.resizeSizeResolver.size()
            requestContext = RequestContext(request, resizeSize)
            firstRequestKey = requestContext.key

            onStart(sketch, requestContext, firstRequestKey)

            // It must be executed after requestDelegate.start(), so that the old request in requestManager will be overwritten.
            val uriString = request.uriString
            if (uriString.isEmpty() || uriString.isBlank()) {
                throw UriInvalidException(uriEmptyMessage)
            }

            val result = RequestInterceptorChain(
                sketch = sketch,
                initialRequest = requestContext.request,
                request = requestContext.request,
                requestContext = requestContext,
                interceptors = sketch.components.getRequestInterceptorList(requestContext.request),
                index = 0,
            ).proceed(requestContext.request)
            val imageData = result.getOrNull()
            if (imageData != null) {
                val lastRequest: ImageRequest = requestContext.request
                val successResult: ImageResult.Success = when {
                    lastRequest is DisplayRequest && imageData is DisplayData -> DisplayResult.Success(
                        request = lastRequest,
                        requestKey = requestContext.key,
                        requestCacheKey = requestContext.cacheKey,
                        drawable = imageData.drawable
                            .tryToResizeDrawable(lastRequest, requestContext.resizeSize),
                        imageInfo = imageData.imageInfo,
                        dataFrom = imageData.dataFrom,
                        transformedList = imageData.transformedList,
                        extras = imageData.extras,
                    )

                    lastRequest is LoadRequest && imageData is LoadData -> LoadResult.Success(
                        request = lastRequest,
                        requestKey = requestContext.key,
                        requestCacheKey = requestContext.cacheKey,
                        bitmap = imageData.bitmap,
                        imageInfo = imageData.imageInfo,
                        dataFrom = imageData.dataFrom,
                        transformedList = imageData.transformedList,
                        extras = imageData.extras,
                    )

                    lastRequest is DownloadRequest && imageData is DownloadData -> DownloadResult.Success(
                        lastRequest, imageData
                    )

                    else -> throw UnsupportedOperationException("Unsupported ImageData: ${imageData::class.java}")
                }
                onSuccess(sketch, requestContext, firstRequestKey, successResult)
                return successResult
            } else {
                val throwable = result.exceptionOrNull()!!
                if (throwable is CancellationException) {
                    throw throwable
                } else {
                    return doError(
                        throwable = throwable,
                        lastRequest = requestContext.request,
                        sketch = sketch,
                        requestContext = requestContext,
                        firstRequestKey = firstRequestKey
                    )
                }
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                onCancel(sketch, requestContext, firstRequestKey, request)
                throw throwable
            } else {
                return doError(
                    throwable = throwable,
                    lastRequest = requestContext?.request ?: request,
                    sketch = sketch,
                    requestContext = requestContext,
                    firstRequestKey = firstRequestKey
                )
            }
        } finally {
            requestContext?.completeCountDrawable("RequestCompleted")
            requestDelegate.finish()
        }
    }

    @MainThread
    private fun onStart(sketch: Sketch, requestContext: RequestContext, firstRequestKey: String) {
        val request = requestContext.request
        request.listener?.onStart(request)
        sketch.logger.d(MODULE) {
            "Request started. '${firstRequestKey}'"
        }
    }

    @MainThread
    private fun onSuccess(
        sketch: Sketch,
        requestContext: RequestContext,
        firstRequestKey: String,
        result: ImageResult.Success
    ) {
        val lastRequest = requestContext.request
        val target = lastRequest.target
        when {
            target is DisplayTarget && result is DisplayResult.Success -> {
                setDrawable(target, result) {
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
        lastRequest.listener?.onSuccess(lastRequest, result)
        sketch.logger.d(MODULE) {
            val logKey = newLogKey(requestContext, firstRequestKey, lastRequest)
            when (result) {
                is DisplayResult.Success -> {
                    "Request Successful. ${result.drawable}. $logKey"
                }

                is LoadResult.Success -> {
                    "Request Successful. ${result.bitmap.logString}. ${result.imageInfo}. ${result.transformedList}. $logKey"
                }

                is DownloadResult.Success -> {
                    "Request Successful. ${result.data}. '${requestContext.key}'. $logKey"
                }

                else -> {
                    "Request Successful. $logKey"
                }
            }
        }
    }

    private fun doError(
        throwable: Throwable,
        lastRequest: ImageRequest,
        sketch: Sketch,
        requestContext: RequestContext?,
        firstRequestKey: String?
    ): ImageResult {
        val errorResult: ImageResult.Error = when (lastRequest) {
            is DisplayRequest -> {
                val errorDrawable = getErrorDrawable(
                    sketch = sketch,
                    request = lastRequest,
                    resizeSize = requestContext?.resizeSize,
                    throwable = throwable
                )
                DisplayResult.Error(lastRequest, errorDrawable, throwable)
            }

            is LoadRequest -> LoadResult.Error(lastRequest, throwable)
            is DownloadRequest -> DownloadResult.Error(lastRequest, throwable)
            else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${lastRequest::class.java}")
        }

        onError(sketch, requestContext, firstRequestKey, lastRequest, errorResult)
        return errorResult
    }

    @MainThread
    private fun onError(
        sketch: Sketch,
        requestContext: RequestContext?,
        firstRequestKey: String?,
        lastRequest: ImageRequest,
        result: ImageResult.Error
    ) {
        val target = lastRequest.target
        val throwable = result.throwable
        when {
            target is DisplayTarget && result is DisplayResult.Error -> {
                setDrawable(target, result) {
                    target.onError(result.drawable)
                }
            }

            target is LoadTarget && result is LoadResult.Error -> {
                target.onError(throwable)
            }

            target is DownloadTarget && result is DownloadResult.Error -> {
                target.onError(throwable)
            }
        }
        lastRequest.listener?.onError(lastRequest, result)
        val logKey = newLogKey(requestContext, firstRequestKey, lastRequest)
        val logMessage = "Request failed. ${throwable.message}. $logKey"
        when (throwable) {
            is DepthException -> sketch.logger.d(MODULE) { logMessage }
            is SketchException -> sketch.logger.e(MODULE, logMessage)
            else -> sketch.logger.e(MODULE, throwable, logMessage)
        }
    }

    @MainThread
    private fun onCancel(
        sketch: Sketch,
        requestContext: RequestContext?,
        firstRequestKey: String?,
        lastRequest: ImageRequest
    ) {
        sketch.logger.d(MODULE) {
            val logKey = newLogKey(requestContext, firstRequestKey, lastRequest)
            "Request canceled. $logKey"
        }
        lastRequest.listener?.onCancel(lastRequest)
    }

    @MainThread
    private fun setDrawable(
        target: Target?,
        result: DisplayResult,
        setDrawable: () -> Unit
    ) {
        if (result.drawable == null) {
            return
        }

        if (target !is TransitionDisplayTarget) {
            setDrawable()
            return
        }

        val fitScale =
            target.asOrNull<ViewDisplayTarget<View>>()?.view.asOrNull<ImageView>()?.fitScale ?: true
        val transition = result.request.transitionFactory?.create(target, result, fitScale)
        if (transition == null) {
            setDrawable()
            return
        }

        transition.transition()
    }

    private fun getErrorDrawable(
        sketch: Sketch,
        request: ImageRequest,
        resizeSize: Size?,
        throwable: Throwable
    ): Drawable? {
        val stateImage =
            if (throwable is UriInvalidException && throwable.message == uriEmptyMessage) {
                request.uriEmpty
            } else {
                request.error
            }
        return (stateImage?.getDrawable(sketch, request, throwable)
            ?: request.placeholder?.getDrawable(sketch, request, throwable))
            ?.tryToResizeDrawable(request, resizeSize)
            ?.toSketchStateDrawable()
    }

    private fun newLogKey(
        requestContext: RequestContext?,
        firstRequestKey: String?,
        lastRequest: ImageRequest
    ): String {
        val firstRequestKey1 = firstRequestKey ?: lastRequest.uriString
        val lastRequestKey = requestContext?.key ?: lastRequest.uriString
        return if (firstRequestKey1 != lastRequestKey) {
            "'${firstRequestKey1}' --> '$lastRequestKey'"
        } else {
            "'${firstRequestKey1}'"
        }
    }
}