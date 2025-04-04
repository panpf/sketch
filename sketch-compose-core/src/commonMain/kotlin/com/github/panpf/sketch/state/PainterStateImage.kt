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
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a [PainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.PainterStateImageTest.testRememberPainterStateImage
 */
@Composable
fun rememberPainterStateImage(painter: EquitablePainter): PainterStateImage =
    remember(painter) { PainterStateImage(painter) }

/**
 * A [StateImage] implemented by Painter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.PainterStateImageTest
 */
@Stable
data class PainterStateImage(val painter: EquitablePainter) : StateImage {

    override val key: String = "Painter(${painter.key})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return painter.asImage()
    }

    override fun toString(): String {
        return "PainterStateImage(painter=${painter.toLogString()})"
    }
}