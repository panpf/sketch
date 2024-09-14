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
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.heightWithBitmapFirst
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.widthWithBitmapFirst

/**
 * Convert [Drawable] to [Image]
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest.testAsImage
 */
fun Drawable.asImage(shareable: Boolean = this !is Animatable): DrawableImage {
    return DrawableImage(this, shareable)
}

/**
 * Convert [Image] to [Drawable], if the conversion fails, throw an exception
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest.testAsDrawable
 */
fun Image.asDrawable(): Drawable = when (this) {
    is DrawableImage -> drawable
    is BitmapImage -> BitmapDrawable(null, bitmap)
    else -> throw IllegalArgumentException("'$this' can't be converted to Drawable")
}

/**
 * Drawable Image
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest
 */
data class DrawableImage internal constructor(
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

    override val cachedInMemory: Boolean = false

    override fun checkValid(): Boolean = true

    override fun toString(): String =
        "DrawableImage(drawable=${drawable.toLogString()}, shareable=$shareable)"
}