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

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Stable
import com.github.panpf.sketch.util.toLogString

/**
 * Drawable painter that implements the [AnimatablePainter] interface
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawableAnimatablePainterTest
 */
@Stable
class DrawableAnimatablePainter(
    drawable: Drawable
) : DrawablePainter(drawable), AnimatablePainter {

    private val animatable: Animatable

    init {
        require(drawable is Animatable) {
            "drawable must be Animatable"
        }
        animatable = drawable
    }

    override fun onFirstRemembered() {
        super.onFirstRemembered()
        animatable.start()
    }

    override fun onLastRemembered() {
        super.onLastRemembered()
        animatable.stop()
    }

    override fun start() {
        if (rememberedCount > 0) {
            animatable.start()
        }
    }

    override fun stop() {
        if (rememberedCount > 0) {
            animatable.stop()
        }
    }

    override fun isRunning(): Boolean {
        return animatable.isRunning
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DrawableAnimatablePainter
        return drawable == other.drawable
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "DrawableAnimatablePainter(drawable=${drawable.toLogString()})"
    }
}