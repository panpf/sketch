package com.github.panpf.sketch.request.internal

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestDepth.NETWORK


internal fun ImageRequest.newCacheKey(): String = buildString {
    append(uriString)
    newQualityKey()?.let {
        append("_").append(it)
    }
    if (disabledAnimationDrawable) {
        append("_").append("DisabledAnimationDrawable")
    }
}

internal fun ImageRequest.newQualityKey(): String? =
    buildList {
        parameters?.cacheKey?.let { add(it) }
        bitmapConfig?.let { add(it.key) }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace?.let { add("colorSpace(${it.name.replace(" ", "")}") }
        }
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed) {
            add("preferQualityOverSpeed")
        }
        resize?.let { add(it.key) }
        transformations?.takeIf { it.isNotEmpty() }?.let { list ->
            add("transformations(${list.joinToString(separator = ",") { it.key }})")
        }
        if (ignoreExifOrientation) {
            add("ignoreExifOrientation")
        }
    }.takeIf { it.isNotEmpty() }
        ?.joinToString(separator = ",", prefix = "Quality(", postfix = ")")

internal fun ImageRequest.newKey(): String = buildString {
    val download: () -> Unit = {
        depth.takeIf { it != NETWORK }?.let {
            append("_").append("RequestDepth(${it})")
        }
        parameters?.key?.takeIf { it.isNotEmpty() }?.let {
            append("_").append(it)
        }
        httpHeaders?.takeIf { !it.isEmpty() }?.let {
            append("_").append(it)
        }
        networkContentDiskCachePolicy.takeIf { it != ENABLED }?.let {
            append("_").append("networkContentDiskCachePolicy($it)")
        }
    }
    val load: () -> Unit = {
        bitmapConfig?.let {
            append("_").append(it.key)
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace?.let {
                append("_").append("colorSpace(${it.name.replace(" ", "")}")
            }
        }
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed) {
            append("_").append("preferQualityOverSpeed")
        }
        resize?.let {
            append("_").append(it.key)
        }
        transformations?.takeIf { it.isNotEmpty() }?.let { list ->
            append("_").append(
                "transformations(${
                    list.joinToString(separator = ",") {
                        it.key.replace("Transformation", "")
                    }
                })"
            )
        }
        if (disabledBitmapPool) {
            append("_").append("disabledBitmapPool")
        }
        if (ignoreExifOrientation) {
            append("_").append("ignoreExifOrientation")
        }
        bitmapResultDiskCachePolicy.takeIf { it != ENABLED }?.let {
            append("_").append("bitmapResultDiskCachePolicy($it)")
        }
    }
    val display: () -> Unit = {
        if (disabledAnimationDrawable) {
            append("_").append("disabledAnimationDrawable")
        }
        bitmapMemoryCachePolicy.takeIf { it != ENABLED }?.let {
            append("_").append("bitmapMemoryCachePolicy($it)")
        }
    }
    when (this@newKey) {
        is DownloadRequest -> {
            append("download")
            append("-").append(uriString)
            download()
        }
        is LoadRequest -> {
            append("load")
            append("-").append(uriString)
            download()
            load()
        }
        is DisplayRequest -> {
            append("display")
            append("-").append(uriString)
            download()
            load()
            display()
        }
        else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${this@newKey::class.java}")
    }
}