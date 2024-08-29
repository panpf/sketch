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
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.resize.Scale

@Stable
class ResizeAnimatablePainter(
    painter: Painter,
    size: Size,
    scale: Scale
) : ResizePainter(painter, size, scale), AnimatablePainter {

    private val animatablePainter: AnimatablePainter

    init {
        require(painter is AnimatablePainter) {
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
        other as ResizeAnimatablePainter
        if (painter != other.painter) return false
        if (size != other.size) return false
        return scale == other.scale
    }

    override fun hashCode(): Int {
        var result = painter.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + scale.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResizeAnimatablePainter(painter=${painter.toLogString()}, size=${size.width}x${size.height}, scale=$scale)"
    }
}