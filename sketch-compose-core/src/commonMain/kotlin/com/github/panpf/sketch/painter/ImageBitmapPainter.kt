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

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.util.toLogString
import kotlin.math.ceil

/**
 * [ImageBitmap] converted to [ImageBitmapPainter]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ImageBitmapPainterTest.testAsPainter
 */
fun ImageBitmap.asPainter(filterQuality: FilterQuality = DrawScope.DefaultFilterQuality): Painter =
    ImageBitmapPainter(this, filterQuality)

/**
 * [ImageBitmap] painter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ImageBitmapPainterTest
 */
@Stable
class ImageBitmapPainter(
    val bitmap: ImageBitmap,
    val filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) : Painter(), SketchPainter {

    override val intrinsicSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())

    private var alpha: Float = DefaultAlpha
    private var colorFilter: ColorFilter? = null

    override fun DrawScope.onDraw() {
        // ceil must be used here instead of round, because when size is a decimal and downward round, the image cannot completely overwrite dst
        val dstSize = IntSize(
            width = ceil(size.width).toInt(),
            height = ceil(size.height).toInt()
        )
        drawImage(
            image = bitmap,
            dstSize = dstSize,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
        )
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ImageBitmapPainter
        if (bitmap != other.bitmap) return false
        if (filterQuality != other.filterQuality) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bitmap.hashCode()
        result = 31 * result + filterQuality.hashCode()
        return result
    }

    override fun toString(): String {
        return "ImageBitmapPainter(bitmap=${bitmap.toLogString()}, filterQuality=$filterQuality)"
    }
}