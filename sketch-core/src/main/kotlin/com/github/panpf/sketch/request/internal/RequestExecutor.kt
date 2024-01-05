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

import android.view.View
import android.widget.ImageView
import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.resizeApplyToDrawable
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.transition.TransitionTarget
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
        val requestDelegate = requestDelegate(sketch, request, coroutineContext.job)
        requestDelegate.assertActive()
        val requestContext = RequestContext(sketch, request)

        try {
            // Set up the request's lifecycle observers. Cancel the request when destroy
            val lifecycle = request.lifecycleResolver.lifecycle()
            requestDelegate.start(lifecycle)

            // Enqueued requests suspend until the lifecycle is started.
            if (enqueue) {
                lifecycle.awaitStarted()
            }

            // resolve resize size
            val resizeSize = request.resizeSizeResolver.size()
            requestContext.resizeSize = resizeSize

            onStart(requestContext)

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
                return doSuccess(requestContext, imageData)
            } else {
                throw result.exceptionOrNull()!!
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                doCancel(requestContext)
                throw throwable
            } else {
                return doError(requestContext, throwable)
            }
        } finally {
            requestContext.completeCountDrawable("RequestCompleted")
            requestDelegate.finish()
        }
    }

    @MainThread
    private fun onStart(requestContext: RequestContext) {
        val request = requestContext.request
        request.listener?.onStart(request)
        requestContext.sketch.logger.d(MODULE) {
            "Request started. '${requestContext.firstRequest.key}'"
        }
    }

    @MainThread
    private fun doSuccess(
        requestContext: RequestContext,
        imageData: ImageData
    ): ImageResult.Success {
        val lastRequest = requestContext.request
        val successImage =
            imageData.image.resizeApplyToDrawable(lastRequest, requestContext.resizeSize)
        val successResult = ImageResult.Success(
            request = lastRequest,
            image = successImage,
            requestKey = requestContext.key,
            requestCacheKey = requestContext.cacheKey,
            imageInfo = imageData.imageInfo,
            dataFrom = imageData.dataFrom,
            transformedList = imageData.transformedList,
            extras = imageData.extras,
        )
        val target = lastRequest.target
        if (target != null) {
            setDrawable(requestContext, target, successResult) {
                target.onSuccess(requestContext, successResult.image)
            }
        }
        lastRequest.listener?.onSuccess(lastRequest, successResult)
        requestContext.sketch.logger.d(MODULE) {
            val logKey = newLogKey(requestContext, requestContext.firstRequest.key, lastRequest)
            "Request Successful. ${successResult.image}. $logKey"
        }
        return successResult
    }

    private fun doError(
        requestContext: RequestContext,
        throwable: Throwable,
    ): ImageResult {
        val sketch = requestContext.sketch
        val lastRequest = requestContext.request
        val errorImage = getErrorDrawable(
            sketch = sketch,
            request = lastRequest,
            throwable = throwable
        )?.resizeApplyToDrawable(lastRequest, requestContext.resizeSize)
        val errorResult: ImageResult.Error = ImageResult.Error(
            request = lastRequest,
            image = errorImage,
            throwable = throwable
        )
        val target = lastRequest.target
        val throwable1 = errorResult.throwable
        if (target != null) {
            setDrawable(requestContext, target, errorResult) {
                target.onError(requestContext, errorResult.image)
            }
        }
        lastRequest.listener?.onError(lastRequest, errorResult)
        val logKey = newLogKey(requestContext, requestContext.firstRequest.key, lastRequest)
        val logMessage = "Request failed. ${throwable1.message}. $logKey"
        when (throwable1) {
            is DepthException -> sketch.logger.d(MODULE) { logMessage }
            is SketchException -> sketch.logger.e(MODULE, logMessage)
            else -> sketch.logger.e(MODULE, throwable1, logMessage)
        }
        return errorResult
    }

    @MainThread
    private fun doCancel(requestContext: RequestContext) {
        val lastRequest = requestContext.request
        requestContext.sketch.logger.d(MODULE) {
            val logKey = newLogKey(requestContext, requestContext.firstRequest.key, lastRequest)
            "Request canceled. $logKey"
        }
        lastRequest.listener?.onCancel(lastRequest)
    }

    @MainThread
    private fun setDrawable(
        requestContext: RequestContext,
        target: Target?,
        result: ImageResult,
        setDrawable: () -> Unit
    ) {
        if (result.image == null) {
            return
        }

        if (target !is TransitionTarget) {
            setDrawable()
            return
        }

        val fitScale =
            target.asOrNull<ViewTarget<View>>()?.view.asOrNull<ImageView>()?.fitScale ?: true
        val transition =
            result.request.transitionFactory?.create(requestContext, target, result, fitScale)
        if (transition == null) {
            setDrawable()
            return
        }

        transition.transition()
    }

    private fun getErrorDrawable(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable
    ): Image? {
        val stateImage =
            if (throwable is UriInvalidException && throwable.message == uriEmptyMessage) {
                request.uriEmpty
            } else {
                request.error
            }
        return (stateImage?.getImage(sketch, request, throwable)
            ?: request.placeholder?.getImage(sketch, request, throwable))
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