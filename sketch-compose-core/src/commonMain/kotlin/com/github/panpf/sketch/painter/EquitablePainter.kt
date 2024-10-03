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

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.key
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

///**
// * Wrap a BitmapPainter into a Painter with equality
// *
// * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
// */
//fun BitmapPainter.asEquitable(): EquitablePainter =
//    EquitablePainter(painter = this, equalityKey = this)
//
///**
// * Wrap a SketchPainter into a Painter with equality
// *
// * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
// */
//fun SketchPainter.asEquitable(): EquitablePainter {
//    return if (this is AnimatablePainter) {
//        EquitableAnimatablePainter(painter = this as Painter, equalityKey = this)
//    } else {
//        EquitablePainter(painter = this as Painter, equalityKey = this)
//    }
//}
//
///**
// * Wrap a SketchPainter into a Painter with equality
// *
// * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
// */
//fun AnimatablePainter.asEquitable(): EquitableAnimatablePainter =
//    EquitableAnimatablePainter(painter = this as Painter, equalityKey = this)

/**
 * The VectorPainter equals returned by two consecutive calls to painterResource() on the same vector drawable resource is false.
 *
 * This will affect the equals of ImageRequest, eventually causing the AsyncImage component to be reorganized to load the image repeatedly.
 *
 * Solve this problem with wrapper
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testEquitablePainter
 */
@Stable
open class EquitablePainter internal constructor(
    painter: Painter,
    val equalityKey: Any,
) : PainterWrapper(painter), Key {

    override val key: String = "EquitablePainter('${key(equalityKey)}')"

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

/**
 * Animatable version of EquitablePainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testEquitableAnimatablePainter
 */
@Stable
class EquitableAnimatablePainter internal constructor(
    painter: Painter,
    equalityKey: Any,
) : EquitablePainter(painter, equalityKey), AnimatablePainter, Key {

    override val key: String = "EquitableAnimatablePainter('${key(equalityKey)}')"

    init {
        check(painter is AnimatablePainter) { "The painter must be implement AnimatablePainter" }
    }

    override fun start() {
        (painter as AnimatablePainter).start()
    }

    override fun stop() {
        (painter as AnimatablePainter).stop()
    }

    override fun isRunning(): Boolean {
        return (painter as AnimatablePainter).isRunning()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EquitableAnimatablePainter
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "EquitableAnimatablePainter(painter=${painter.toLogString()}, equalityKey=$equalityKey)"
    }
}