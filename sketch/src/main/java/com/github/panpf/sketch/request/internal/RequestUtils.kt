package com.github.panpf.sketch.request.internal

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestDepth.NETWORK

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
    if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed == true) {
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
    if (ignoreExifOrientation == true) {
        appendQueryParameter("_ignoreExifOrientation", true.toString())
    }
    if (disabledAnimatedImage == true) {
        appendQueryParameter("_disabledAnimatedImage", true.toString())
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
    downloadDiskCachePolicy.takeIf { it != ENABLED }?.let {
        appendQueryParameter("_downloadDiskCachePolicy", it.toString())
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
        if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed == true) {
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
        if (disabledReuseBitmap == true) {
            appendQueryParameter("_disabledReuseBitmap", true.toString())
        }
        if (ignoreExifOrientation == true) {
            appendQueryParameter("_ignoreExifOrientation", true.toString())
        }
        bitmapResultDiskCachePolicy.takeIf { it != ENABLED }?.let {
            appendQueryParameter("_bitmapResultDiskCachePolicy", it.name)
        }
    }

    if (this@newKey is DisplayRequest) {
        if (disabledAnimatedImage == true) {
            appendQueryParameter("_disabledAnimatedImage", true.toString())
        }
        bitmapMemoryCachePolicy.takeIf { it != ENABLED }?.let {
            appendQueryParameter("_bitmapMemoryCachePolicy", it.name)
        }
    }
}.build().toString()