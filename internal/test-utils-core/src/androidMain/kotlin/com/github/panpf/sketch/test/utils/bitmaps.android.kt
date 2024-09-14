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

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.simpleName

fun Bitmap.toSizeString(): String = "${width}x${height}"

@Suppress("USELESS_ELVIS")
val Bitmap.configOrNull: Bitmap.Config?
    get() = config ?: null

fun Bitmap.toShortInfoString(): String =
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        "Bitmap(${width}x${height},$configOrNull,${colorSpace?.simpleName})"
    } else {
        "Bitmap(${width}x${height},$configOrNull)"
    }

val Bitmap.size: Size
    get() = Size(width, height)

fun shortInfoColorSpaceName(name: String): String {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        ",${name}"
    } else {
        ""
    }
}

fun logColorSpaceName(name: String): String {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        ",${name}"
    } else {
        ""
    }
}