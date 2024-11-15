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
import com.github.panpf.sketch.util.toLogString

/**
 * Convert [Drawable] to [Image]
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest.testAsImage
 */
fun Drawable.asImage(shareable: Boolean = this !is Animatable): DrawableImage {
    return DrawableImage(this, shareable)
}

/**
 * Convert [Image] to [Drawable], if the conversion fails, return null
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest.testAsDrawableOrNull
 */
fun Image.asDrawableOrNull(): Drawable? = when (this) {
    is DrawableImage -> drawable
    is BitmapImage -> BitmapDrawable(null, bitmap)
    else -> null
}

/**
 * Convert [Image] to [Drawable], if the conversion fails, throw an exception
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest.testAsDrawable
 */
fun Image.asDrawable(): Drawable =
    asDrawableOrNull() ?: throw IllegalArgumentException("Unable to convert '$this' to Drawable")

/**
 * Drawable Image
 *
 * @see com.github.panpf.sketch.core.android.test.DrawableImageTest
 */
data class DrawableImage constructor(
    val drawable: Drawable,
    override val shareable: Boolean = drawable !is Animatable
) : Image {

    override val width: Int = drawable.intrinsicWidth

    override val height: Int = drawable.intrinsicHeight

    override val byteCount: Long by lazy {
        when (drawable) {
            is BitmapDrawable -> drawable.bitmap.byteCount.toLong()
            is ByteCountProvider -> drawable.byteCount
            else -> 4L * drawable.intrinsicWidth * drawable.intrinsicHeight    // Estimate 4 bytes per pixel.
        }
    }

    override fun checkValid(): Boolean {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap.isRecycled.not()
        } else {
            true
        }
    }

    override fun toString(): String =
        "DrawableImage(drawable=${drawable.toLogString()}, shareable=$shareable)"
}