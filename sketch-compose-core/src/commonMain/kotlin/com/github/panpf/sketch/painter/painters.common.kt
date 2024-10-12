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

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.VectorPainter
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.NullableKey
import com.github.panpf.sketch.util.toLogString

/**
 * Convert the painter to a log string
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.PaintersTest.testPainterKey
 */
fun Painter.key(equalityKey: Any? = null): String {
    if (this is Key) {
        return key
    }
    if (this is NullableKey && key != null) {
        val key = key
        if (key != null) {
            return key
        }
    }
    if (this is BitmapPainter) {
        return if (equalityKey != null) {
            "BitmapPainter:$equalityKey"
        } else {
            "BitmapPainter(${intrinsicSize.toLogString()})"
        }
    }
    if (this is ColorPainter) {
        return "ColorPainter(${color.toArgb()})"
    }
    if (this is VectorPainter) {
        return if (equalityKey != null) {
            "VectorPainter:$equalityKey"
        } else {
            "VectorPainter(${intrinsicSize.toLogString()})"
        }
    }
    return if (equalityKey != null) {
        "${this}:$equalityKey"
    } else {
        this.toString()
    }
}

/**
 * Convert the painter to a log string
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.PaintersTest.testPainterToLogString
 */
fun Painter.toLogString(): String = when (this) {
    is SketchPainter -> toString()
    is BitmapPainter -> "BitmapPainter(size=${intrinsicSize.toLogString()})"
    is ColorPainter -> "ColorPainter(color=${color.toArgb()})"
    is VectorPainter -> "VectorPainter(size=${intrinsicSize.toLogString()})"
//    is PainterWrapper -> "PainterWrapper(painter=${painter.toLogString()})"
    else -> toString()
}