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
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.buildSuccessResult
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive

/**
 * An [Interceptor] that loads and displays a thumbnail image first
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.ThumbnailInterceptorTest
 */
class ThumbnailInterceptor : Interceptor {

    companion object {
        const val SORT_WEIGHT = 60
        const val KEY_THUMBNAIL = "sketch#thumbnail"
        const val KEY_FROM_THUMBNAIL = "sketch#fromThumbnail"
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    @MainThread
    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        val requestContext = chain.requestContext
        val request = chain.request
        val target = request.target
        val thumbnail: Any? = request.extras?.get(KEY_THUMBNAIL)
        return if (thumbnail != null && target != null) {
            coroutineScope {
                val thumbnailTask = async {
                    loadThumbnail(
                        coroutineScope = this@async,
                        requestContext = requestContext,
                        request = request,
                        target = target,
                        thumbnail = thumbnail
                    )
                }

                val result = async {
                    chain.proceed(request)
                }.await()
                if (thumbnailTask.isActive) {
                    thumbnailTask.cancel()
                }
                result
            }
        } else {
            chain.proceed(request)
        }
    }

    private suspend fun loadThumbnail(
        coroutineScope: CoroutineScope,
        requestContext: RequestContext,
        request: ImageRequest,
        target: Target,
        thumbnail: Any,
    ) {
        val sketch = requestContext.sketch
        sketch.logger.d {
            "Request thumbnail started. '${requestContext.logKey}'"
        }

        val thumbnailRequest = newThumbnailRequest(request, target, thumbnail)
        if (thumbnailRequest == null) {
            sketch.logger.w {
                "Request thumbnail failed. Thumbnail extra must be String or ImageRequest. '$thumbnail'. '${requestContext.logKey}'"
            }
            return
        }

        val thumbnailRequestContext = RequestContext(
            sketch = sketch,
            initialRequest = thumbnailRequest,
            size = requestContext.size,
        )
        val thumbnailResult = InterceptorChain(
            requestContext = thumbnailRequestContext,
            interceptors = sketch.components.getInterceptors(thumbnailRequest),
            index = 0,
        ).proceed(thumbnailRequest)
        val thumbnailImageData = thumbnailResult.getOrNull()

        if (!coroutineScope.isActive) {
            sketch.logger.i {
                "Request thumbnail canceled. '${requestContext.logKey}'"
            }
        } else if (thumbnailImageData == null) {
            sketch.logger.w(tr = thumbnailResult.exceptionOrNull()) {
                "Request thumbnail failed. ${thumbnailResult.exceptionOrNull()}. '${requestContext.logKey}'"
            }
        } else {
            displayThumbnailImage(
                request = request,
                thumbnailRequestContext = thumbnailRequestContext,
                thumbnailImageData = thumbnailImageData,
                target = target,
            )
            sketch.logger.d {
                "Request thumbnail successful. $thumbnailImageData '${requestContext.logKey}'"
            }
        }
    }

    private fun newThumbnailRequest(
        request: ImageRequest,
        target: Target,
        thumbnail: Any,
    ): ImageRequest? {
        val commonConfig: ImageRequest.Builder.() -> Unit = {
            // Avoid entering ThumbnailInterceptor again and causing an infinite loop
            removeExtra(key = KEY_THUMBNAIL)

            // Avoid displaying placeholder images repeatedly
            placeholder(stateImage = null)

            // Using a wrapped Target, clear the listener from the Target and the cache key of the original request
            target(target = ThumbnailTarget(target))
        }

        return when (thumbnail) {
            is ImageRequest -> thumbnail.newRequest {
                commonConfig()
            }

            is String -> request.newRequest(uri = thumbnail) {
                commonConfig()

                // The cache key for the original request is configured for the original uri and does not apply to thumbnail requests.
                memoryCacheKey(key = null)
                resultCacheKey(key = null)
                downloadCacheKey(key = null)

                // Clear listeners and progress listeners to avoid being affected by the original request
                clearListeners()
                clearProgressListeners()
            }

            else -> null
        }
    }

    private fun displayThumbnailImage(
        request: ImageRequest,
        thumbnailRequestContext: RequestContext,
        thumbnailImageData: ImageData,
        target: Target,
    ) {
        val thumbnailImage = thumbnailImageData.image.resizeOnDraw(
            request = request,
            size = thumbnailRequestContext.size
        )
        val result = buildSuccessResult(
            requestContext = thumbnailRequestContext,
            request = request,
            imageData = thumbnailImageData,
            image = thumbnailImage,
        )
        val sketch = thumbnailRequestContext.sketch
        setupImageWithTransition(sketch, request, target, result) {
            target.onSuccess(sketch, request, result, result.image)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "ThumbnailInterceptor"
}

///**
// * Check if the request is a thumbnail request
// *
// * @see com.github.panpf.sketch.core.common.test.request.internal.ThumbnailInterceptorTest.testThumbnailRequest
// */
//fun ImageRequest.isThumbnailRequest(): Boolean =
//    this.extras?.get(ThumbnailInterceptor.KEY_FROM_THUMBNAIL) == true
//
///**
// * Mark or unmark the request as a thumbnail request
// *
// * @see com.github.panpf.sketch.core.common.test.request.internal.ThumbnailInterceptorTest.testThumbnailRequest
// */
//internal fun ImageRequest.Builder.markThumbnailRequest(mark: Boolean = true): ImageRequest.Builder =
//    apply {
//        if (mark) {
//            this.setExtra(
//                key = ThumbnailInterceptor.KEY_FROM_THUMBNAIL,
//                value = true,
//                cacheKey = null,
//                requestKey = null
//            )
//        } else {
//            this.removeExtra(key = ThumbnailInterceptor.KEY_FROM_THUMBNAIL)
//        }
//    }