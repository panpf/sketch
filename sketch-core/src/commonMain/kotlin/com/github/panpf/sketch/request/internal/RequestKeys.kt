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

import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.net.toUri
import com.github.panpf.sketch.Key
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

internal fun ImageRequest.newKey(): String = ImageRequestKeyBuilder(this)
    .appendDepth()
    .appendParameters()
    .appendHttpHeaders()
    .appendDownloadCachePolicy()
    .appendBitmapConfig()
    .appendColorSpace()
    .appendPreferQualityOverSpeed()
    .appendSize()
    .appendPrecision()
    .appendScale()
    .appendTransformations()
    .appendDisallowReuseBitmap()
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
    .appendBitmapConfig()
    .appendColorSpace()
    .appendPreferQualityOverSpeed()
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

    private val uri = request.uriString.toUri().buildUpon()

    fun appendDepth(): ImageRequestKeyBuilder = apply {
        request.depth.takeIf { it != NETWORK }?.also { depth ->
            uri.appendQueryParameter("_depth", depth.name)
        }
    }

    fun appendParameters(): ImageRequestKeyBuilder = apply {
        request.parameters?.key?.takeIf { it.isNotEmpty() }?.also { parameterKey ->
            uri.appendQueryParameter("_parameters", parameterKey)
        }
    }

    fun appendCacheParameters(): ImageRequestKeyBuilder = apply {
        request.parameters?.cacheKey?.takeIf { it.isNotEmpty() }?.also { parameterKey ->
            uri.appendQueryParameter("_parameters", parameterKey)
        }
    }

    fun appendHttpHeaders(): ImageRequestKeyBuilder = apply {
        request.httpHeaders?.takeIf { !it.isEmpty() }?.also {
            uri.appendQueryParameter("_httpHeaders", it.toString())
        }
    }

    fun appendDownloadCachePolicy(): ImageRequestKeyBuilder = apply {
        request.downloadCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            uri.appendQueryParameter("_downloadCachePolicy", cachePolicy.name)
        }
    }

    fun appendBitmapConfig(): ImageRequestKeyBuilder = apply {
        request.bitmapConfig?.also { bitmapConfig ->
            uri.appendQueryParameter("_bitmapConfig", bitmapConfig.key)
        }
    }

    fun appendColorSpace(): ImageRequestKeyBuilder = apply {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request.colorSpace?.also { colorSpace ->
                uri.appendQueryParameter("_colorSpace", colorSpace.name.replace(" ", "_"))
            }
        }
    }

    fun appendPreferQualityOverSpeed(): ImageRequestKeyBuilder = apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT <= VERSION_CODES.M && request.preferQualityOverSpeed) {
            uri.appendQueryParameter("_preferQualityOverSpeed", true.toString())
        }
    }

    fun appendSize(size: Size? = null): ImageRequestKeyBuilder = apply {
        if (size != null) {
            uri.appendQueryParameter("_size", size.toString())
        } else {
            uri.appendQueryParameter("_size", request.resizeSizeResolver.key)
        }
    }

    fun appendPrecision(): ImageRequestKeyBuilder = apply {
        uri.appendQueryParameter("_precision", request.resizePrecisionDecider.key)
    }

    fun appendScale(): ImageRequestKeyBuilder = apply {
        uri.appendQueryParameter("_scale", request.resizeScaleDecider.key)
    }

    fun appendTransformations(): ImageRequestKeyBuilder = apply {
        request.transformations?.takeIf { it.isNotEmpty() }?.also { list ->
            val transformationKeys = list
                .joinToString(prefix = "[", postfix = "]", separator = ",") {
                    it.key.replace("Transformation", "")
                }
            uri.appendQueryParameter("_transformations", transformationKeys)
        }
    }

    fun appendDisallowReuseBitmap(): ImageRequestKeyBuilder = apply {
        if (request.disallowReuseBitmap) {
            uri.appendQueryParameter("_disallowReuseBitmap", true.toString())
        }
    }

    fun appendIgnoreExifOrientation(): ImageRequestKeyBuilder = apply {
        if (request.ignoreExifOrientation) {
            uri.appendQueryParameter("_ignoreExifOrientation", true.toString())
        }
    }

    fun appendResultCachePolicy(): ImageRequestKeyBuilder = apply {
        request.resultCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            uri.appendQueryParameter("_resultCachePolicy", cachePolicy.name)
        }
    }

    fun appendDecoders(): ImageRequestKeyBuilder = apply {
        request.componentRegistry?.decoderFactoryList.orEmpty()
            .mapNotNull { it.key.takeIf { key -> key != Key.INVALID_KEY } }
            .takeIf { it.isNotEmpty() }
            ?.also { list ->
                val decoderKeys = list.joinToString(prefix = "[", postfix = "]", separator = ",")
                uri.appendQueryParameter("_decoders", decoderKeys)
            }
    }

    fun appendDecodeInterceptors(): ImageRequestKeyBuilder = apply {
        request.componentRegistry?.decodeInterceptorList.orEmpty()
            .mapNotNull { it.key.takeIf { key -> key != Key.INVALID_KEY } }
            .takeIf { it.isNotEmpty() }
            ?.also { list ->
                val decodeInterceptorKeys =
                    list.joinToString(prefix = "[", postfix = "]", separator = ",")
                uri.appendQueryParameter("_decodeInterceptors", decodeInterceptorKeys)
            }
    }

    fun appendDisallowAnimatedImage(): ImageRequestKeyBuilder = apply {
        if (request.disallowAnimatedImage) {
            uri.appendQueryParameter("_disallowAnimatedImage", true.toString())
        }
    }

    fun appendResizeApplyToDrawable(): ImageRequestKeyBuilder = apply {
        if (request.resizeApplyToDrawable) {
            uri.appendQueryParameter("_resizeApplyToDrawable", true.toString())
        }
    }

    fun appendMemoryCachePolicy(): ImageRequestKeyBuilder = apply {
        request.memoryCachePolicy.takeIf { it != ENABLED }?.also { cachePolicy ->
            uri.appendQueryParameter("_memoryCachePolicy", cachePolicy.name)
        }
    }

    fun appendRequestInterceptors(): ImageRequestKeyBuilder = apply {
        request.componentRegistry?.requestInterceptorList.orEmpty()
            .mapNotNull { it.key.takeIf { key -> key != Key.INVALID_KEY } }
            .takeIf { it.isNotEmpty() }
            ?.also { list ->
                val requestInterceptorKeys =
                    list.joinToString(prefix = "[", postfix = "]", separator = ",")
                uri.appendQueryParameter("_requestInterceptors", requestInterceptorKeys)
            }
    }

    fun build(): String = uri.build().toString().let { Uri.decode(it) }
}