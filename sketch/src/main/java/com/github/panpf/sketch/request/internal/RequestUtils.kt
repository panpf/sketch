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

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest

internal fun ImageRequest.newCacheKey(): String = uri.buildUpon().apply {
    parameters?.cacheKey?.takeIf { it.isNotEmpty() }?.let {
        appendQueryParameter("_parameters", it)
    }
    bitmapConfig?.let {
        appendQueryParameter("_bitmapConfig", it.key)
    }
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        colorSpace?.let {
            appendQueryParameter("_colorSpace", it.name.replace(" ", "_"))
        }
    }
    @Suppress("DEPRECATION")
    if (preferQualityOverSpeed) {
        appendQueryParameter("_preferQualityOverSpeed", true.toString())
    }
    resize?.let {
        appendQueryParameter("_resize", it.key)
    }
    transformations?.takeIf { it.isNotEmpty() }?.let { list ->
        appendQueryParameter(
            "_transformations",
            list.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }
        )
    }
    if (ignoreExifOrientation) {
        appendQueryParameter("_ignoreExifOrientation", true.toString())
    }
    if (disallowAnimatedImage) {
        appendQueryParameter("_disallowAnimatedImage", true.toString())
    }
}.build().toString()

internal fun ImageRequest.newKey(): String = uri.buildUpon().apply {
    depth.takeIf { it != NETWORK }?.let {
        appendQueryParameter("_depth", it.toString())
    }
    parameters?.key?.takeIf { it.isNotEmpty() }?.let {
        appendQueryParameter("_parameters", it)
    }
    httpHeaders?.takeIf { !it.isEmpty() }?.let {
        appendQueryParameter("_httpHeaders", it.toString())
    }
    downloadCachePolicy.takeIf { it != ENABLED }?.let {
        appendQueryParameter("_downloadCachePolicy", it.toString())
    }

    if (this@newKey is LoadRequest || this@newKey is DisplayRequest) {
        bitmapConfig?.let {
            appendQueryParameter("_bitmapConfig", it.key)
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace?.let {
                appendQueryParameter("_colorSpace", it.name.replace(" ", "_"))
            }
        }
        @Suppress("DEPRECATION")
        if (preferQualityOverSpeed) {
            appendQueryParameter("_preferQualityOverSpeed", true.toString())
        }
        resize?.let {
            appendQueryParameter("_resize", it.key)
        }
        transformations?.takeIf { it.isNotEmpty() }?.let { list ->
            appendQueryParameter(
                "_transformations",
                list.joinToString(prefix = "[", postfix = "]", separator = ",") {
                    it.key.replace("Transformation", "")
                }
            )
        }
        if (disallowReuseBitmap) {
            appendQueryParameter("_disallowReuseBitmap", true.toString())
        }
        if (ignoreExifOrientation) {
            appendQueryParameter("_ignoreExifOrientation", true.toString())
        }
        resultCachePolicy.takeIf { it != ENABLED }?.let {
            appendQueryParameter("_resultCachePolicy", it.name)
        }
    }

    if (this@newKey is DisplayRequest) {
        if (disallowAnimatedImage) {
            appendQueryParameter("_disallowAnimatedImage", true.toString())
        }
        memoryCachePolicy.takeIf { it != ENABLED }?.let {
            appendQueryParameter("_memoryCachePolicy", it.name)
        }
    }
}.build().toString()