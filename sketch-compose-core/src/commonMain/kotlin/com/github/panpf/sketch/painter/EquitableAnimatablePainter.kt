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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.key

/**
 * Animatable version of EquitablePainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitableAnimatablePainterTest
 */
@Stable
class EquitableAnimatablePainter constructor(
    painter: Painter,
    equalityKey: Any,
) : EquitablePainter(painter, equalityKey), AnimatablePainter, Key {

    override val key: String = "EquitableAnimatablePainter('${key(equalityKey)}')"

    private val animatablePainter: AnimatablePainter

    init {
        check(painter is AnimatablePainter) {
            "painter must be AnimatablePainter"
        }
        animatablePainter = painter
    }

    override fun start() {
        animatablePainter.start()
    }

    override fun stop() {
        animatablePainter.stop()
    }

    override fun isRunning(): Boolean {
        return animatablePainter.isRunning()
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