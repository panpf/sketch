/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch

import android.content.res.Resources
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Stable
import com.github.panpf.sketch.cache.AndroidBitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.scale
import com.github.panpf.sketch.util.toLogString

fun AndroidBitmap.asSketchImage(
    resources: Resources? = null,
    shareable: Boolean = isImmutable
): AndroidBitmapImage {
    return AndroidBitmapImage(this, shareable, resources)
}

fun Image.getBitmapOrNull(): AndroidBitmap? = when (this) {
    is AndroidBitmapImage -> bitmap
    is AndroidDrawableImage -> drawable.asOrNull<BitmapDrawable>()?.bitmap
    else -> null
}

fun Image.getBitmapOrThrow(): AndroidBitmap = getBitmapOrNull()
    ?: throw IllegalArgumentException("Unable to get Bitmap from Image '$this'")

@Stable
data class AndroidBitmapImage internal constructor(
    val bitmap: AndroidBitmap,
    override val shareable: Boolean = !bitmap.isMutable,
    val resources: Resources? = null
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = bitmap.byteCount.toLong()

    override val allocationByteCount: Long = bitmap.allocationByteCountCompat.toLong()

    override fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value =
        AndroidBitmapImageValue(this, extras)

    override fun checkValid(): Boolean = !bitmap.isRecycled

    override fun transformer(): ImageTransformer = BitmapImageTransformer()

    override fun getPixels(): IntArray {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return pixels
    }

    override fun toString(): String =
        "AndroidBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

class BitmapImageTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = image.asOrThrow<AndroidBitmapImage>().bitmap
        val outBitmap = inputBitmap.scale(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = image.asOrThrow<AndroidBitmapImage>().bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}