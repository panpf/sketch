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

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.util.calculateScaleMultiplierWithFit

/**
 * Create a [ResizePainter] and remember it
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ResizePainterTest.testRememberResizePainter
 */
@Composable
fun rememberResizePainter(painter: Painter, size: Size, scale: Scale = CENTER_CROP): ResizePainter {
    return remember(painter, size, scale) {
        painter.resize(size, scale)
    }
}

/**
 * Resize the [Painter] to the specified size
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ResizePainterTest.testPainterResize
 */
fun Painter.resize(size: Size, scale: Scale = CENTER_CROP): ResizePainter {
    return if (this is AnimatablePainter) {
        ResizeAnimatablePainter(this, size, scale)
    } else {
        ResizePainter(this, size, scale)
    }
}

/**
 * Resize the [Painter] to the specified size
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ResizePainterTest
 */
@Stable
open class ResizePainter(
    val painter: Painter,
    val size: Size,
    val scale: Scale
) : Painter(), RememberObserver, SketchPainter {

    override val intrinsicSize: Size = size

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    override fun onRemembered() {
        (painter as? RememberObserver)?.onRemembered()
        (painter as? AnimatablePainter)?.start()
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        (painter as? AnimatablePainter)?.stop()
        (painter as? RememberObserver)?.onForgotten()
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun DrawScope.onDraw() {
        with(painter) {
            val dstSize: Size = this@onDraw.size
            val srcSize: Size = this@with.intrinsicSize
            val scaledSrcSize = computeScaledSize(srcSize, dstSize)
            if (dstSize.isUnspecified || dstSize.isEmpty()) {
                draw(scaledSrcSize, alpha, colorFilter)
            } else {
                val (horizontal, vertical) = when (scale) {
                    Scale.START_CROP -> 0f to 0f
                    Scale.END_CROP -> dstSize.width - scaledSrcSize.width to dstSize.height - scaledSrcSize.height
                    else -> (dstSize.width - scaledSrcSize.width) / 2f to (dstSize.height - scaledSrcSize.height) / 2f
                }
                inset(horizontal = horizontal, vertical = vertical) {
                    draw(scaledSrcSize, alpha, colorFilter)
                }
            }
        }
    }

    private fun computeScaledSize(srcSize: Size, dstSize: Size): Size {
        if (srcSize.isUnspecified || srcSize.isEmpty()) return dstSize
        if (dstSize.isUnspecified || dstSize.isEmpty()) return dstSize
        val sizeMultiplier = calculateScaleMultiplierWithFit(
            srcWidth = srcSize.width,
            srcHeight = srcSize.height,
            dstWidth = dstSize.width,
            dstHeight = dstSize.height,
            fitScale = false
        )
        return Size(
            width = srcSize.width * sizeMultiplier,
            height = srcSize.height * sizeMultiplier
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResizePainter
        if (painter != other.painter) return false
        if (size != other.size) return false
        return scale == other.scale
    }

    override fun hashCode(): Int {
        var result = painter.hashCode()
        // Because size are value classes, they will be replaced by long.
        // Long will lose precision when converting hashCode, causing the hashCode generated by different srcOffset and srcSize to be the same.
        result = 31 * result + size.toString().hashCode()
        result = 31 * result + scale.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResizePainter(painter=${painter.toLogString()}, size=${size.width}x${size.height}, scale=$scale)"
    }
}