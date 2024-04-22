/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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

import androidx.compose.runtime.Stable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.StateImage

fun PainterStateImage(painter: PainterEqualWrapper): PainterStateImage = PainterStateImageImpl(painter)


@Stable
interface PainterStateImage : StateImage {
    val painter: PainterEqualWrapper
}

private class PainterStateImageImpl(override val painter: PainterEqualWrapper) : PainterStateImage {

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image? {
        return painter.painter.asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PainterStateImage) return false
        return painter == other.painter
    }

    override fun hashCode(): Int {
        return painter.hashCode()
    }

    override fun toString(): String {
        return "PainterStateImage(painter=$painter)"
    }
}