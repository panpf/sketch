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

import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

internal fun ImageRequest.newKey(): String = ImageRequestKeyBuilder(this)
    .appendDepth()
    .appendRequestExtras()
    .appendHttpHeaders()
    .appendDownloadCachePolicy()
    .appendSize()
    .appendSizeMultiplier()
    .appendPrecision()
    .appendScale()
    .appendTransformations()
    .appendResultCachePolicy()
    .appendDisallowAnimatedImage()
    .appendResizeOnDraw()
    .appendAllowNullImage()
    .appendMemoryCachePolicy()
    .appendTransitionFactory()
    .appendPlaceholder()
    .appendFallback()
    .appendError()
    .appendDecoders()
    .appendDecodeInterceptors()
    .appendRequestInterceptors()
    .build()

internal fun ImageRequest.newCacheKey(size: Size): String = ImageRequestKeyBuilder(this)
    .appendCacheExtras()
    .appendSize(size)
    .appendSizeMultiplier()
    .appendPrecision()
    .appendScale()
    .appendTransformations()
    .appendDisallowAnimatedImage()
    .appendDecoders()
    .appendDecodeInterceptors()
    .appendRequestInterceptors()
    .build()

private class ImageRequestKeyBuilder(private val request: ImageRequest) {

    private val keyBuilder = StringBuilder(request.uri.toString())

    private fun appendQueryParameter(name: String, value: String) {
        val askIndex = keyBuilder.lastIndexOf("?")
        if (askIndex != -1) {
            if (askIndex != keyBuilder.length - 1) {
                keyBuilder.append("&")
            }
            keyBuilder.append("$name=$value")
        } else {
            keyBuilder.append("?")
            keyBuilder.append("$name=$value")
        }
    }

    fun appendDepth(): ImageRequestKeyBuilder = apply {
        request.depthHolder.takeIf { it.depth != NETWORK }?.also { depth ->
            appendQueryParameter("_depth", depth.key)
        }
    }

    fun appendRequestExtras(): ImageRequestKeyBuilder = apply {
        request.extras?.requestKey?.takeIf { it.isNotEmpty() }?.also { parameterKey ->
            appendQueryParameter("_extras", parameterKey)
        }
    }

    fun appendCacheExtras(): ImageRequestKeyBuilder = apply {
        request.extras?.cacheKey?.takeIf { it.isNotEmpty() }?.also { parameterKey ->
            appendQueryParameter("_extras", parameterKey)
        }
    }

    fun appendHttpHeaders(): ImageRequestKeyBuilder = apply {
        request.httpHeaders?.takeIf { !it.isEmpty() }?.also {
            appendQueryParameter("_httpHeaders", it.toString())
        }
    }

    fun appendDownloadCachePolicy(): ImageRequestKeyBuilder = apply {
        request.downloadCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            appendQueryParameter("_downloadCachePolicy", cachePolicy.name)
        }
    }

    fun appendSize(size: Size? = null): ImageRequestKeyBuilder = apply {
        if (size != null) {
            appendQueryParameter("_size", size.toString())
        } else {
            appendQueryParameter("_size", request.sizeResolver.key)
        }
    }

    fun appendSizeMultiplier(): ImageRequestKeyBuilder = apply {
        val sizeMultiplier = request.sizeMultiplier
        if (sizeMultiplier != null) {
            appendQueryParameter("_sizeMultiplier", sizeMultiplier.toString())
        }
    }

    fun appendPrecision(): ImageRequestKeyBuilder = apply {
        appendQueryParameter("_precision", request.precisionDecider.key)
    }

    fun appendScale(): ImageRequestKeyBuilder = apply {
        appendQueryParameter("_scale", request.scaleDecider.key)
    }

    fun appendTransformations(): ImageRequestKeyBuilder = apply {
        request.transformations?.takeIf { it.isNotEmpty() }?.also { list ->
            val transformationKeys = list
                .joinToString(prefix = "[", postfix = "]", separator = ",") {
                    it.key.replace("Transformation", "")
                }
            appendQueryParameter("_transformations", transformationKeys)
        }
    }

    fun appendResultCachePolicy(): ImageRequestKeyBuilder = apply {
        request.resultCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            appendQueryParameter("_resultCachePolicy", cachePolicy.name)
        }
    }

    fun appendDecoders(): ImageRequestKeyBuilder = apply {
        request.componentRegistry?.decoderFactoryList.orEmpty()
            .map { it.key }
            .takeIf { it.isNotEmpty() }
            ?.also { list ->
                val decoderKeys = list.joinToString(prefix = "[", postfix = "]", separator = ",")
                appendQueryParameter("_decoders", decoderKeys)
            }
    }

    fun appendDecodeInterceptors(): ImageRequestKeyBuilder = apply {
        request.componentRegistry?.decodeInterceptorList.orEmpty()
            .mapNotNull { it.key }
            .takeIf { it.isNotEmpty() }
            ?.also { list ->
                val decodeInterceptorKeys =
                    list.joinToString(prefix = "[", postfix = "]", separator = ",")
                appendQueryParameter("_decodeInterceptors", decodeInterceptorKeys)
            }
    }

    fun appendDisallowAnimatedImage(): ImageRequestKeyBuilder = apply {
        if (request.disallowAnimatedImage) {
            appendQueryParameter("_disallowAnimatedImage", true.toString())
        }
    }

    fun appendResizeOnDraw(): ImageRequestKeyBuilder = apply {
        val resizeOnDraw = request.resizeOnDraw
        if (resizeOnDraw == true) {
            appendQueryParameter("_resizeOnDraw", true.toString())
        }
    }

    fun appendMemoryCachePolicy(): ImageRequestKeyBuilder = apply {
        request.memoryCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            appendQueryParameter("_memoryCachePolicy", cachePolicy.name)
        }
    }

    fun appendTransitionFactory(): ImageRequestKeyBuilder = apply {
        val transitionFactory = request.transitionFactory
        if (transitionFactory != null) {
            appendQueryParameter("_transitionFactory", transitionFactory.key)
        }
    }

    fun appendRequestInterceptors(): ImageRequestKeyBuilder = apply {
        request.componentRegistry?.requestInterceptorList.orEmpty()
            .mapNotNull { it.key }
            .takeIf { it.isNotEmpty() }
            ?.also { list ->
                val requestInterceptorKeys =
                    list.joinToString(prefix = "[", postfix = "]", separator = ",")
                appendQueryParameter("_requestInterceptors", requestInterceptorKeys)
            }
    }

    fun appendPlaceholder(): ImageRequestKeyBuilder = apply {
        val placeholder = request.placeholder
        if (placeholder != null) {
            appendQueryParameter("_placeholder", placeholder.key)
        }
    }

    fun appendFallback(): ImageRequestKeyBuilder = apply {
        val fallback = request.fallback
        if (fallback != null) {
            appendQueryParameter("_fallback", fallback.key)
        }
    }

    fun appendError(): ImageRequestKeyBuilder = apply {
        val error = request.error
        if (error != null) {
            appendQueryParameter("_error", error.key)
        }
    }

    fun appendAllowNullImage(): ImageRequestKeyBuilder = apply {
        val allowNullImage = request.allowNullImage
        if (allowNullImage == true) {
            appendQueryParameter("_allowNullImage", true.toString())
        }
    }

    fun build(): String = keyBuilder.toString()
}