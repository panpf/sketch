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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.util.Equalizer
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.key

fun Painter.asEquality(equalKey: Any): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = equalKey)

fun ColorPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = this.color.value)

fun BrushPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = this)

fun BitmapPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this, equalityKey = this)

fun SketchPainter.asEquality(): PainterEqualizer =
    PainterEqualizer(wrapped = this as Painter, equalityKey = this)

/**
 * The VectorPainter equals returned by two consecutive calls to painterResource() on the same vector drawable resource is false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 */
@Stable
class PainterEqualizer(
    override val wrapped: Painter,
    override val equalityKey: Any,
    private val equalityKeyString: String = key(equalityKey)
) : Equalizer<Painter>, Key {

    override val key: String = equalityKeyString

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PainterEqualizer
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "PainterEqualizer(wrapped=${wrapped.toLogString()}, equalityKey=$equalityKeyString)"
    }
}