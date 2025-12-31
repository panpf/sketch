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
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.util.copyWith
import com.github.panpf.sketch.util.simpleName

fun shortInfoColorSpace(name: String): String {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        ",${name}"
    } else {
        ""
    }
}

fun logColorSpace(name: String): String {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        ",${name}"
    } else {
        ""
    }
}

fun colorSpaceNameCompat(name: String = "SRGB"): String {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        name
    } else {
        "SRGB"
    }
}

val Bitmap.colorSpaceNameCompat: String?
    get() = if (VERSION.SDK_INT >= VERSION_CODES.O) {
        colorSpace?.simpleName
    } else {
        "SRGB"
    }

actual fun createBitmap(width: Int, height: Int): Bitmap {
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
}

actual fun createARGBBitmap(width: Int, height: Int): Bitmap {
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
}

actual fun create565Bitmap(width: Int, height: Int): Bitmap {
    return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
}

actual fun getMutableBitmap(): Bitmap {
    return ResourceImages.jpeg.decode().bitmap.copyWith(isMutable = true)
}

actual fun getImmutableBitmap(): Bitmap {
    return ResourceImages.jpeg.decode().bitmap
}

actual fun Bitmap.toPreviewBitmap(): Any = this

fun ColorType.expectedRgb565(mimeType: String): ColorType {
    return if (this == ColorType.RGB_565
        && (mimeType == "image/png" || mimeType == "image/gif")
    ) {
        ColorType.ARGB_8888
    } else {
        this
    }
}

actual val defaultColorType: ColorType
    get() = Bitmap.Config.ARGB_8888