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

package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.painter.PainterEqualizer
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a [PainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.PainterStateImageTest.testRememberPainterStateImage
 */
@Composable
fun rememberPainterStateImage(painter: PainterEqualizer): PainterStateImage =
    remember(painter) { PainterStateImage(painter) }

/**
 * A [StateImage] implemented by Painter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.PainterStateImageTest
 */
@Stable
class PainterStateImage(val painter: PainterEqualizer) : StateImage {

    override val key: String = "PainterStateImage(${painter.key})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return painter.wrapped.asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PainterStateImage
        return painter == other.painter
    }

    override fun hashCode(): Int {
        return painter.hashCode()
    }

    override fun toString(): String {
        return "PainterStateImage(painter=$painter)"
    }
}