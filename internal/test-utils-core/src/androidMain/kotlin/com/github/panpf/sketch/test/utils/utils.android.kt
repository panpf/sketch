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
import android.graphics.BitmapRegionDecoder
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import java.io.InputStream

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

fun InputStream.newBitmapRegionDecoderInstanceCompat(): BitmapRegionDecoder? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        BitmapRegionDecoder.newInstance(this)
    } else {
        @Suppress("DEPRECATION")
        BitmapRegionDecoder.newInstance(this, false)
    }

val Drawable.alphaCompat: Int
    get() = DrawableCompat.getAlpha(this)

fun ImageRequest.decode(sketch: Sketch): DecodeResult {
    val request = this@decode
    val fetchResult = runBlocking {
        sketch.components.newFetcherOrThrow(
            request
                .toRequestContext(sketch, Size.Empty)
        ).fetch()
    }.getOrThrow()
    val requestContext = runBlocking {
        request.toRequestContext(sketch)
    }
    return BitmapFactoryDecoder(
        requestContext = requestContext,
        dataSource = fetchResult.dataSource.asOrThrow()
    ).decode()
}

@Suppress("DEPRECATION")
val PackageInfo.versionCodeCompat: Int
    get() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        longVersionCode.toInt()
    } else {
        versionCode
    }