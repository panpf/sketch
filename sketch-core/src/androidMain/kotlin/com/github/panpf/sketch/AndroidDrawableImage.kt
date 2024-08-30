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

package com.github.panpf.sketch

import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.drawable.heightWithBitmapFirst
import com.github.panpf.sketch.drawable.toLogString
import com.github.panpf.sketch.drawable.widthWithBitmapFirst
import com.github.panpf.sketch.util.allocationByteCountCompat

/**
 * Convert [Drawable] to [Image]
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest.testAsSketchImage
 */
fun Drawable.asSketchImage(shareable: Boolean = this !is Animatable): AndroidDrawableImage {
    return AndroidDrawableImage(this, shareable)
}

/**
 * Convert [Image] to [Drawable], if the conversion fails, return null
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest.testGetDrawableOrNull
 */
fun Image.getDrawableOrNull(): Drawable? = when (this) {
    is AndroidBitmapImage -> null
    is AndroidDrawableImage -> drawable
    else -> null
}

/**
 * Convert [Image] to [Drawable], if the conversion fails, throw an exception
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest.testGetDrawableOrThrow
 */
fun Image.getDrawableOrThrow(): Drawable = getDrawableOrNull()
    ?: throw IllegalArgumentException("Unable to get Drawable from Image '$this'")

/**
 * Convert [Image] to [Drawable], if the conversion fails, return null
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest.testAsDrawableOrNull
 */
fun Image.asDrawableOrNull(): Drawable? = when (this) {
    is AndroidBitmapImage -> BitmapDrawable(resources, bitmap)
    is AndroidDrawableImage -> drawable
    else -> null
}

/**
 * Convert [Image] to [Drawable], if the conversion fails, throw an exception
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest.testAsDrawableOrThrow
 */
fun Image.asDrawableOrThrow(): Drawable = asDrawableOrNull()
    ?: throw IllegalArgumentException("'$this' can't be converted to Drawable")

/**
 * Android Drawable Image
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidDrawableImageTest
 */
data class AndroidDrawableImage internal constructor(
    val drawable: Drawable,
    override val shareable: Boolean = drawable !is Animatable
) : Image {

    override val width: Int = drawable.intrinsicWidth

    override val height: Int = drawable.intrinsicHeight

    override val byteCount: Long = when (drawable) {
        is ByteCountProvider -> drawable.byteCount
        is BitmapDrawable -> drawable.bitmap.byteCount.toLong()
        else -> 4L * drawable.widthWithBitmapFirst * drawable.heightWithBitmapFirst    // Estimate 4 bytes per pixel.
    }

    override val allocationByteCount: Long = when (drawable) {
        is ByteCountProvider -> drawable.allocationByteCount
        is BitmapDrawable -> drawable.bitmap.allocationByteCountCompat.toLong()
        else -> 4L * drawable.widthWithBitmapFirst * drawable.heightWithBitmapFirst    // Estimate 4 bytes per pixel.
    }

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun toString(): String =
        "AndroidDrawableImage(drawable=${drawable.toLogString()}, shareable=$shareable)"

    override fun getPixels(): IntArray? {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            return pixels
        }
        return null
    }
}