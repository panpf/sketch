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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.toLogString

/**
 * [ComposeBitmap] converted to [ComposeBitmapPainter]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ComposeBitmapPainterTest.testComposeBitmapAsPainter
 */
fun ComposeBitmap.asPainter(filterQuality: FilterQuality = DrawScope.DefaultFilterQuality): Painter =
    ComposeBitmapPainter(this, filterQuality)

/**
 * [ComposeBitmap] painter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.ComposeBitmapPainterTest
 */
@Stable
class ComposeBitmapPainter(
    val bitmap: ComposeBitmap,
    val filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) : Painter(), SketchPainter {

    override val intrinsicSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())

    override fun DrawScope.onDraw() {
        val intSize = IntSize(size.width.toInt(), size.height.toInt())
        drawImage(bitmap, dstSize = intSize, filterQuality = filterQuality)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ComposeBitmapPainter
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
        return "ComposeBitmapPainter(bitmap=${bitmap.toLogString()}, filterQuality=$filterQuality)"
    }
}