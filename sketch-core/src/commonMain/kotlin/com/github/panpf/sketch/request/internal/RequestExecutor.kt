/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.awaitStarted
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.job
import kotlin.coroutines.coroutineContext

/**
 * All requests start and end here
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.RequestExecutorTest
 */
class RequestExecutor constructor(val sketch: Sketch) {

    companion object {
        private const val URI_EMPTY_MESSAGE = "Request uri is empty or blank"
    }

    @MainThread
    suspend fun execute(request: ImageRequest, enqueue: Boolean): ImageResult {
        requiredMainThread()

        // Wrap the request to manage its lifecycle.
        val requestDelegate = requestDelegate(sketch, request, coroutineContext.job)
        requestDelegate.assertActive()

        val request1 = applyGlobalOptions(sketch, request)
        var requestContext: RequestContext? = null
        try {
            // Set up the request's lifecycle observers. Cancel the request when destroy
            val lifecycle = request1.lifecycleResolver.lifecycle()
            requestDelegate.start(lifecycle)

            // Enqueued requests suspend until the lifecycle is started.
            if (enqueue) {
                lifecycle.awaitStarted()
            }

            requestContext = RequestContext(sketch, request1)
            onStart(requestContext)

            // It must be executed after requestDelegate.start(), so that the old request in requestManager will be overwritten.
            val uri = request1.uri.toString()
            if (uri.isEmpty() || uri.isBlank()) {
                throw UriInvalidException(URI_EMPTY_MESSAGE)
            }

            val result = RequestInterceptorChain(
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
                doCancel(sketch, request1, requestContext)
                throw throwable
            } else {
                return doError(sketch, request1, requestContext, throwable)
            }
        } finally {
            requestDelegate.finish()
        }
    }

    private fun applyGlobalOptions(sketch: Sketch, request: ImageRequest): ImageRequest {
        val defaultImageOptions = request.defaultOptions
        val globalImageOptions = sketch.globalImageOptions
        return if (globalImageOptions != null) {
            val newDefaultOptions =
                if (defaultImageOptions != null && defaultImageOptions !== globalImageOptions) {
                    defaultImageOptions.merged(globalImageOptions)
                } else {
                    globalImageOptions
                }
            request.newBuilder().defaultOptions(newDefaultOptions).build()
        } else {
            request
        }
    }

    @MainThread
    private fun onStart(requestContext: RequestContext) {
        val request = requestContext.request
        request.listener?.onStart(request)
        requestContext.sketch.logger.d {
            "Request started. '${requestContext.logKey}'"
        }
    }

    @MainThread
    private fun doSuccess(
        requestContext: RequestContext,
        imageData: ImageData
    ): ImageResult.Success {
        val lastRequest = requestContext.request
        val successImage = imageData.image.resizeOnDraw(lastRequest, requestContext.size)
        val result = ImageResult.Success(
            request = lastRequest,
            image = successImage,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            imageInfo = imageData.imageInfo,
            dataFrom = imageData.dataFrom,
            resize = imageData.resize,
            transformeds = imageData.transformeds,
            extras = imageData.extras,
        )
        val target = lastRequest.target
        val sketch = requestContext.sketch
        if (target != null) {
            setImage(sketch, lastRequest, target, result) {
                target.onSuccess(sketch, lastRequest, result, result.image)
            }
        }
        lastRequest.listener?.onSuccess(lastRequest, result)
        sketch.logger.d {
            val resultString = "image=${result.image}, " +
                    "imageInfo=${result.imageInfo}, " +
                    "dataFrom=${result.dataFrom}, " +
                    "resize=${result.resize}, " +
                    "transformeds=${result.transformeds}, " +
                    "extras=${result.extras}"
            "Request Successful. Result($resultString). '${requestContext.logKey}'"
        }
        return result
    }

    private fun doError(
        sketch: Sketch,
        request: ImageRequest,
        requestContext: RequestContext?,
        throwable: Throwable,
    ): ImageResult {
        val lastRequest = requestContext?.request ?: request
        val errorImage = getErrorDrawable(
            sketch = sketch,
            request = lastRequest,
            throwable = throwable
        )?.resizeOnDraw(lastRequest, requestContext?.size)
        val errorResult: ImageResult.Error = ImageResult.Error(
            request = lastRequest,
            image = errorImage,
            throwable = throwable
        )
        val target = lastRequest.target
        val throwable1 = errorResult.throwable
        if (target != null) {
            setImage(sketch, lastRequest, target, errorResult) {
                target.onError(sketch, lastRequest, errorResult, errorResult.image)
            }
        }
        lastRequest.listener?.onError(lastRequest, errorResult)
        val logMessage =
            "Request failed. '${throwable1.message}'. '${
                requestContext?.logKey ?: request.newCacheKey(
                    null
                )
            }'"
        when (throwable1) {
            is DepthException -> sketch.logger.d { logMessage }
            is SketchException -> sketch.logger.e(logMessage)
            else -> sketch.logger.e(throwable1, logMessage)
        }
        return errorResult
    }

    @MainThread
    private fun doCancel(sketch: Sketch, request: ImageRequest, requestContext: RequestContext?) {
        val lastRequest = requestContext?.request ?: request
        sketch.logger.d {
            "Request canceled. '${requestContext?.logKey ?: request.newCacheKey(null)}'"
        }
        lastRequest.listener?.onCancel(lastRequest)
    }

    @MainThread
    private fun setImage(
        sketch: Sketch,
        request: ImageRequest,
        target: Target?,
        result: ImageResult,
        setImage: () -> Unit
    ) {
        if (result.image == null) {
            return
        }

        if (target == null) {
            setImage()
            return
        }

        val transition =
            result.request.transitionFactory?.create(sketch, request, target, result)
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
            if (throwable is UriInvalidException && throwable.message == URI_EMPTY_MESSAGE) {
                request.fallback
            } else {
                request.error
            }
        return (stateImage?.getImage(sketch, request, throwable)
            ?: request.placeholder?.getImage(sketch, request, throwable))
    }
}