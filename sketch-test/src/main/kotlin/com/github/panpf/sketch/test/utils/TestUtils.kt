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
package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapRegionDecoder
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.graphics.drawable.DrawableCompat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.InputStream
import java.math.BigDecimal

var sketchCount = 0

/* 低版本（大概是 21 及以下版本同一个文件不能打开多次，因此必须使用不同的缓存目录）*/
fun Context.newTestDiskCacheDirectory(): File {
    return File(externalCacheDir ?: cacheDir, DiskCache.DEFAULT_DIR_NAME + "${sketchCount++}")
}

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

val ImageInfo.size: Size
    get() = Size(width, height)

val Size.ratio: Float
    get() = (width / height.toFloat()).format(1)

val Bitmap.size: Size
    get() = Size(width, height)

fun InputStream.newBitmapRegionDecoderInstanceCompat(): BitmapRegionDecoder? =
    if (VERSION.SDK_INT >= VERSION_CODES.S) {
        BitmapRegionDecoder.newInstance(this)
    } else {
        @Suppress("DEPRECATION")
        BitmapRegionDecoder.newInstance(this, false)
    }

val Drawable.alphaCompat: Int
    get() = DrawableCompat.getAlpha(this)

fun samplingByTarget(imageSize: Size, targetSize: Size, mimeType: String? = null): Size {
    val sampleSize = calculateSampleSize(imageSize, targetSize, false)
    return calculateSampledBitmapSize(imageSize, sampleSize, mimeType)
}

fun ImageRequest.toRequestContext(resizeSize: Size? = null): RequestContext {
    return RequestContext(this, resizeSize ?: runBlocking { resizeSizeResolver.size() })
}

fun LoadRequest.decode(sketch: Sketch): BitmapDecodeResult {
    val request = this@decode
    val fetchResult = runBlocking {
        sketch.components.newFetcherOrThrow(request).fetch()
    }.getOrThrow()
    return DefaultBitmapDecoder(
        sketch = sketch,
        requestContext = request.toRequestContext(),
        dataSource = fetchResult.dataSource.asOrThrow()
    ).let { runBlocking { it.decode() }.getOrThrow() }
}

/**
 * Convert to the type specified by the generic
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

internal fun Float.format(newScale: Int): Float =
    BigDecimal(toDouble()).setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()