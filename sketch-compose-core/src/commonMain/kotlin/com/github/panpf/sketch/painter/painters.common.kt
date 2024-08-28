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

import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorPainter
import com.github.panpf.sketch.Image

expect fun Image.asPainter(): Painter

fun Painter.toLogString(): String = when (this) {
    is SketchPainter -> toString()
    is BitmapPainter -> "BitmapPainter@${hashCode().toString(16)}(${toSizeString()})"
    is ColorPainter -> "ColorPainter@${hashCode().toString(16)}(${color})"
    is BrushPainter -> "BrushPainter@${hashCode().toString(16)}(${brush})"
    is VectorPainter -> "VectorPainter@${hashCode().toString(16)}(${toSizeString()})"
    else -> platformToLogString() ?: toString()
}

expect fun Painter.platformToLogString(): String?

internal fun Painter.toSizeString(): String =
    if (intrinsicSize.isSpecified) "$intrinsicSize" else "unspecified"