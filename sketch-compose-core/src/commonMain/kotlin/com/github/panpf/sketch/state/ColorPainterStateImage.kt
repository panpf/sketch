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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a [ColorPainterStateImage] instance and remember it
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest.testRememberColorPainterStateImage
 */
@Composable
fun rememberColorPainterStateImage(color: Color): ColorPainterStateImage =
    remember(color) { ColorPainterStateImage(color) }

/**
 * Create a [ColorPainterStateImage] instance and remember it
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest.testRememberColorPainterStateImage
 */
@Composable
fun rememberColorPainterStateImageWithLong(color: Long): ColorPainterStateImage =
    remember(color) { ColorPainterStateImage(Color(color)) }

/**
 * Create a [ColorPainterStateImage] instance and remember it
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest.testRememberColorPainterStateImage
 */
@Composable
fun rememberColorPainterStateImageWithInt(color: Int): ColorPainterStateImage =
    remember(color) { ColorPainterStateImage(Color(color)) }

/**
 * StateImage implemented by ColorPainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest
 */
@Stable
data class ColorPainterStateImage(val color: Color) : StateImage {

    override val key: String = "ColorPainterStateImage(${color.value})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return ColorPainter(color).asImage()
    }

    override fun toString(): String {
        return "ColorPainterStateImage(color=${color.value})"
    }
}