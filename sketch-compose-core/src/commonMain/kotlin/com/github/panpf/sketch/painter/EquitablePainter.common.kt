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

@file:JvmName("EqualsPainterKt")
@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.util.Key
import kotlin.jvm.JvmName

/**
 * Wrap a Painter into a Painter with equality
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
 */
fun Painter.asEquitable(equalityKey: Any): EquitablePainter {
    return if (this is AnimatablePainter) {
        EquitableAnimatablePainter(painter = this, equalityKey = equalityKey)
    } else {
        EquitablePainter(painter = this, equalityKey = equalityKey)
    }
}


/**
 * Wrap a ColorPainter into a Painter with equality
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
 */
fun ColorPainter.asEquitable(): EquitablePainter =
    EquitablePainter(painter = this, equalityKey = this.color.value)

/**
 * Wrap a BrushPainter into a Painter with equality
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
 */
fun BrushPainter.asEquitable(): EquitablePainter =
    EquitablePainter(painter = this, equalityKey = this)

/**
 * The VectorPainter equals returned by two consecutive calls to painterResource() on the same vector drawable resource is false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest
 */
@Stable
open class EquitablePainter constructor(
    painter: Painter,
    val equalityKey: Any,
) : PainterWrapper(painter), SketchPainter, Key {

    override val key: String = painter.key(equalityKey)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EquitablePainter
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "EquitablePainter(painter=${painter.toLogString()}, equalityKey=$equalityKey)"
    }
}