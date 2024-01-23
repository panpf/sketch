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

import androidx.annotation.MainThread
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.resize.sizeApplyToDraw
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.awaitStarted
import com.github.panpf.sketch.transition.TransitionTarget
import com.github.panpf.sketch.util.SketchException
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
            val size = request.sizeResolver.size()
            requestContext.size = size

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
            requestDelegate.finish()
            requestContext.completed()
        }
    }

    @MainThread
    private fun onStart(requestContext: RequestContext) {
        val request = requestContext.request
        request.listener?.onStart(request)
        requestContext.sketch.logger.d(MODULE) {
            "Request started. '${requestContext.initialRequest.key}'"
        }
    }

    @MainThread
    private fun doSuccess(
        requestContext: RequestContext,
        imageData: ImageData
    ): ImageResult.Success {
        val lastRequest = requestContext.request
        val successImage =
            imageData.image.sizeApplyToDraw(lastRequest, requestContext.size)
        val result = ImageResult.Success(
            request = lastRequest,
            image = successImage,
            cacheKey = requestContext.cacheKey,
            imageInfo = imageData.imageInfo,
            dataFrom = imageData.dataFrom,
            transformedList = imageData.transformedList,
            extras = imageData.extras,
        )
        val target = lastRequest.target
        if (target != null) {
            setImage(requestContext, target, result) {
                target.onSuccess(requestContext, result.image)
            }
        }
        lastRequest.listener?.onSuccess(lastRequest, result)
        requestContext.sketch.logger.d(MODULE) {
            val resultString = "image=${result.image}, " +
                    "imageInfo=${result.imageInfo}, " +
                    "dataFrom=${result.dataFrom}, " +
                    "transformedList=${result.transformedList}, " +
                    "extras=${result.extras}"
            "Request Successful. Result($resultString). '${requestContext.logKey}'"
        }
        return result
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
        )?.sizeApplyToDraw(lastRequest, requestContext.size)
        val errorResult: ImageResult.Error = ImageResult.Error(
            request = lastRequest,
            image = errorImage,
            throwable = throwable
        )
        val target = lastRequest.target
        val throwable1 = errorResult.throwable
        if (target != null) {
            setImage(requestContext, target, errorResult) {
                target.onError(requestContext, errorResult.image)
            }
        }
        lastRequest.listener?.onError(lastRequest, errorResult)
        val logMessage = "Request failed. '${throwable1.message}'. '${requestContext.logKey}'"
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
            "Request canceled. '${requestContext.logKey}'"
        }
        lastRequest.listener?.onCancel(lastRequest)
    }

    @MainThread
    private fun setImage(
        requestContext: RequestContext,
        target: Target?,
        result: ImageResult,
        setImage: () -> Unit
    ) {
        if (result.image == null) {
            return
        }

        if (target !is TransitionTarget) {
            setImage()
            return
        }

        val transition =
            result.request.transitionFactory?.create(requestContext, target, result)
        if (transition == null) {
            setImage()
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
}