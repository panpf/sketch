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

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.graphics.BitmapRegionDecoder
import android.graphics.drawable.Drawable
import android.os.Build
import com.github.panpf.sketch.util.Size
import java.io.InputStream

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

fun InputStream.newBitmapRegionDecoderInstanceCompat(): BitmapRegionDecoder? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        BitmapRegionDecoder.newInstance(this)
    } else {
        @Suppress("DEPRECATION")
        BitmapRegionDecoder.newInstance(this, false)
    }

@Suppress("DEPRECATION")
val PackageInfo.versionCodeCompat: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode.toInt()
    } else {
        versionCode
    }

@SuppressLint("AnnotateVersionCheck")
fun isVersionAtLeast(api: Int): Boolean = Build.VERSION.SDK_INT >= api