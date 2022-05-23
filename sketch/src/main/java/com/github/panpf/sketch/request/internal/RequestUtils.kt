package com.github.panpf.sketch.request.internal

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.Depth.NETWORK

internal fun ImageRequest.newCacheKey(): String = uri.buildUpon().apply {
    parameters?.key?.takeIf { it.isNotEmpty() }?.let {
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
        appendQueryParameter("_transformations", list.joinToString(separator = ",") {
            it.key.replace("Transformation", "")
        })
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
            appendQueryParameter("_transformations", list.joinToString(separator = ",") {
                it.key.replace("Transformation", "")
            })
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