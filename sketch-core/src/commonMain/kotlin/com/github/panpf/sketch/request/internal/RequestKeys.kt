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

import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

internal fun ImageRequest.newKey(): String = ImageRequestKeyBuilder(this)
    .appendDepth()
    .appendParameters()
    .appendHttpHeaders()
    .appendDownloadCachePolicy()
    .appendSize()
    .appendPrecision()
    .appendScale()
    .appendTransformations()
    .appendIgnoreExifOrientation()
    .appendResultCachePolicy()
    .appendDisallowAnimatedImage()
    .appendResizeApplyToDrawable()
    .appendMemoryCachePolicy()
    .appendDecoders()
    .appendDecodeInterceptors()
    .appendRequestInterceptors()
    .build()

internal fun ImageRequest.newCacheKey(size: Size): String = ImageRequestKeyBuilder(this)
    .appendCacheParameters()
//    .appendBitmapConfig()
//    .appendColorSpace()
//    .appendPreferQualityOverSpeed()
    .appendSize(size)
    .appendPrecision()
    .appendScale()
    .appendTransformations()
    .appendIgnoreExifOrientation()
    .appendDisallowAnimatedImage()
    .appendDecoders()
    .appendDecodeInterceptors()
    .appendRequestInterceptors()
    .build()

private class ImageRequestKeyBuilder(private val request: ImageRequest) {

    private val keyBuilder = StringBuilder(request.uriString)

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
        request.depth.takeIf { it != NETWORK }?.also { depth ->
            appendQueryParameter("_depth", depth.name)
        }
    }

    fun appendParameters(): ImageRequestKeyBuilder = apply {
        request.parameters?.key?.takeIf { it.isNotEmpty() }?.also { parameterKey ->
            appendQueryParameter("_parameters", parameterKey)
        }
    }

    fun appendCacheParameters(): ImageRequestKeyBuilder = apply {
        request.parameters?.cacheKey?.takeIf { it.isNotEmpty() }?.also { parameterKey ->
            appendQueryParameter("_parameters", parameterKey)
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

//    fun appendBitmapConfig(): ImageRequestKeyBuilder = apply {
//        request.bitmapConfig?.also { bitmapConfig ->
//            appendQueryParameter("_bitmapConfig", bitmapConfig.key)
//        }
//    }
//
//    fun appendColorSpace(): ImageRequestKeyBuilder = apply {
//        if (VERSION.SDK_INT >= VERSION_CODES.O) {
//            request.colorSpace?.also { colorSpace ->
//                appendQueryParameter("_colorSpace", colorSpace.name.replace(" ", "_"))
//            }
//        }
//    }
//
//    fun appendPreferQualityOverSpeed(): ImageRequestKeyBuilder = apply {
//        @Suppress("DEPRECATION")
//        if (VERSION.SDK_INT <= VERSION_CODES.M && request.preferQualityOverSpeed) {
//            appendQueryParameter("_preferQualityOverSpeed", true.toString())
//        }
//    }

    fun appendSize(size: Size? = null): ImageRequestKeyBuilder = apply {
        if (size != null) {
            appendQueryParameter("_size", size.toString())
        } else {
            appendQueryParameter("_size", request.sizeResolver.key)
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

    fun appendIgnoreExifOrientation(): ImageRequestKeyBuilder = apply {
        if (request.ignoreExifOrientation) {
            appendQueryParameter("_ignoreExifOrientation", true.toString())
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

    fun appendResizeApplyToDrawable(): ImageRequestKeyBuilder = apply {
        if (request.sizeApplyToDraw) {
            appendQueryParameter("_resizeApplyToDrawable", true.toString())
        }
    }

    fun appendMemoryCachePolicy(): ImageRequestKeyBuilder = apply {
        request.memoryCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            appendQueryParameter("_memoryCachePolicy", cachePolicy.name)
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

    fun build(): String = keyBuilder.toString()
}