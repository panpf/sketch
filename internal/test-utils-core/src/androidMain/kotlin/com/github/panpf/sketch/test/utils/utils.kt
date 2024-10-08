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

package com.github.panpf.sketch.test.utils

import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.BitmapRegionDecoder
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import kotlin.math.pow
import kotlin.math.round

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

val Size.ratio: Float
    get() = (width / height.toFloat()).format(1)

val Bitmap.size: Size
    get() = Size(width, height)

fun InputStream.newBitmapRegionDecoderInstanceCompat(): BitmapRegionDecoder? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
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

fun ImageRequest.decode(sketch: Sketch): DecodeResult {
    val request = this@decode
    val fetchResult = runBlocking {
        sketch.components.newFetcherOrThrow(request).fetch()
    }.getOrThrow()
    val requestContext = runBlocking {
        request.toRequestContext(sketch)
    }
    return BitmapFactoryDecoder(
        requestContext = requestContext,
        dataSource = fetchResult.dataSource.asOrThrow()
    ).let { runBlocking { it.decode() }.getOrThrow() }
}

/**
 * Convert to the type specified by the generic
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

@Suppress("DEPRECATION")
val PackageInfo.versionCodeCompat: Int
    get() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        longVersionCode.toInt()
    } else {
        versionCode
    }