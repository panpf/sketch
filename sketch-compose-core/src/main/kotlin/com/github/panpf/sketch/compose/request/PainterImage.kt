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
package com.github.panpf.sketch.compose.request

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ByteCountProvider
import com.github.panpf.sketch.CountingImage
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.internal.RequestContext
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlin.math.roundToInt

fun Painter.asSketchImage(shareable: Boolean = false): Image {
    return PainterImage(this, shareable)
}

fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is BitmapImage -> BitmapPainter(bitmap.asImageBitmap())
    is DrawableImage -> DrawablePainter(drawable.mutate())
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}

data class PainterImage(val painter: Painter, override val shareable: Boolean = false) : Image {

    override val width: Int
        get() = painter.intrinsicSize.width.roundToInt()

    override val height: Int
        get() = painter.intrinsicSize.height.roundToInt()

    override val byteCount: Int
        get() = when (painter) {
            is DrawablePainter -> {
                when (val drawable = painter.drawable) {
                    is ByteCountProvider -> drawable.byteCount
                    is BitmapDrawable -> drawable.bitmap.byteCount
                    else -> 4 * width * height    // Estimate 4 bytes per pixel.
                }
            }

            else -> 4 * width * height
        }

    override val allocationByteCount: Int
        get() = when (painter) {
            is DrawablePainter -> {
                when (val drawable = painter.drawable) {
                    is ByteCountProvider -> drawable.allocationByteCount
                    is BitmapDrawable -> drawable.bitmap.allocationByteCount
                    else -> 4 * width * height    // Estimate 4 bytes per pixel.
                }
            }

            else -> 4 * width * height
        }

    override fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): Value? = null

    override fun checkValid(): Boolean = true

    override fun toCountingImage(requestContext: RequestContext): CountingImage? = null
}